package org.wita.erp.services.transaction.order;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.wita.erp.domain.entities.status.PaymentStatus;
import org.wita.erp.domain.entities.transaction.order.Order;
import org.wita.erp.domain.entities.transaction.order.Receivable;
import org.wita.erp.domain.entities.transaction.order.dtos.CreateReceivableRequestDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.ReceivableDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.UpdateReceivableRequestDTO;
import org.wita.erp.domain.entities.transaction.order.mappers.ReceivableMapper;
import org.wita.erp.domain.repositories.transaction.order.OrderRepository;
import org.wita.erp.domain.repositories.transaction.order.ReceivableRepository;
import org.wita.erp.infra.exceptions.order.OrderException;
import org.wita.erp.infra.exceptions.payable.PayableException;
import org.wita.erp.infra.exceptions.receivable.ReceivableException;
import org.wita.erp.infra.schedules.handler.ScheduledTaskTypes;
import org.wita.erp.infra.schedules.scheduler.SchedulerService;
import org.wita.erp.services.transaction.order.observers.CreateReceivableOrderObserver;
import org.wita.erp.services.transaction.order.observers.ReceivableCompensationObserver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceivableService {
    private final ReceivableRepository receivableRepository;
    private final OrderRepository orderRepository;
    private final ReceivableMapper receivableMapper;
    private final SchedulerService schedulerService;
    private final ApplicationEventPublisher publisher;


    public ResponseEntity<Page<ReceivableDTO>> getAllReceivable(Pageable pageable, String searchTerm) {
        Page<Receivable> receivablePage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            receivablePage = receivableRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            receivablePage = receivableRepository.findAll(pageable);
        }

        return ResponseEntity.ok(receivablePage.map(receivableMapper::toDTO));
    }

    @Transactional
    public ResponseEntity<List<ReceivableDTO>> save(CreateReceivableRequestDTO data){
        Order order = orderRepository.findById(data.order())
                .orElseThrow(() -> new OrderException("Order not registered in the system", HttpStatus.NOT_FOUND));

        if (order.getCustomerPaymentType().getIsImmediate()){
            throw new PayableException("Cannot create receivable for immediate payment orders", HttpStatus.BAD_REQUEST);
        }

        LocalDate firstDueDate = LocalDate.now().plusDays(30);
        BigDecimal installmentValue = order.getValue().divide(BigDecimal.valueOf(order.getInstallments()), 2, RoundingMode.HALF_UP);

        List<Receivable> receivables = new java.util.ArrayList<>(List.of());

        for (int i = 1; i <= order.getInstallments(); i++) {
            Receivable receivable = new Receivable();
            receivable.setOrder(order);
            receivable.setPaymentStatus(data.paymentStatus());
            receivable.setDueDate(firstDueDate.plusDays(30L *i));
            receivable.setInstallment(i);
            receivable.setValue(installmentValue);
            receivableRepository.save(receivable);

            schedulerService.schedule(
                    ScheduledTaskTypes.RECEIVABLE_DUE_SOON,
                    receivable.getId().toString(),
                    receivable.getDueDate().minusDays(3).atStartOfDay()
            );

            schedulerService.schedule(
                    ScheduledTaskTypes.RECEIVABLE_OVERDUE,
                    receivable.getId().toString(),
                    receivable.getDueDate().atStartOfDay()
            );

            receivables.add(receivable);
        }

        List<ReceivableDTO> dtos = receivables.stream()
                .map(receivableMapper::toDTO)
                .toList();


        return ResponseEntity.ok(dtos);
    }

    public ResponseEntity<ReceivableDTO> update(UUID id, UpdateReceivableRequestDTO data) {
        Receivable receivable = receivableRepository.findById(id)
                .orElseThrow(() -> new ReceivableException("Receivable not found", HttpStatus.NOT_FOUND));

        receivableMapper.updateReceivableFromDTO(data, receivable);
        receivableRepository.save(receivable);

        return ResponseEntity.ok(receivableMapper.toDTO(receivable));
    }

    public ResponseEntity<ReceivableDTO> delete(UUID id) {
        Receivable receivable = receivableRepository.findById(id)
                .orElseThrow(() -> new PayableException("Receivable not found", HttpStatus.NOT_FOUND));
        receivable.setActive(false);
        receivableRepository.save(receivable);
        return ResponseEntity.ok(receivableMapper.toDTO(receivable));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    @Async
    public void onReceivableOrderCreated(CreateReceivableOrderObserver event) {
        try{
            orderRepository.findById(event.order())
                    .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));

            CreateReceivableRequestDTO dto = new CreateReceivableRequestDTO(
                    PaymentStatus.PENDING,
                    event.order()
            );

            this.save(dto);

        } catch (Exception e) {
            publisher.publishEvent(new ReceivableCompensationObserver(event.order()));
            throw new ReceivableException("Failed to process stock movements for order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    public void onReceivableOrderUpdated(CreateReceivableOrderObserver event) {
        Order order = orderRepository.findById(event.order())
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));

        if (order.getCustomerPaymentType().getIsImmediate() || !order.getCustomerPaymentType().getAllowsInstallments()){
            throw new ReceivableException("Cannot update receivable for this payment method", HttpStatus.BAD_REQUEST);
        }

        List<Receivable> receivables = receivableRepository.findByOrderId(event.order());

        // Order installments updated
        if (order.getInstallments() != receivables.size()) {
            receivables.forEach(
                    receivable -> {
                        this.delete(receivable.getId());
                    }
            );

            this.save(new CreateReceivableRequestDTO(
                    PaymentStatus.PENDING,
                    event.order()
            ));
        }

        // Order items updated
        if (!Objects.equals(order.getValue(), receivables.stream().map(Receivable::getValue).reduce(BigDecimal.ZERO, BigDecimal::add))) {
            receivables
                    .forEach(receivable -> {
                        receivable.setValue(order.getValue().divide(BigDecimal.valueOf(order.getInstallments()), 2, RoundingMode.HALF_UP));
                        receivableRepository.save(receivable);
                    });
        }
    }
}
