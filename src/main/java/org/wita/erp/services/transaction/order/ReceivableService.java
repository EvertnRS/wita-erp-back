package org.wita.erp.services.transaction.order;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.transaction.order.Order;
import org.wita.erp.domain.entities.transaction.order.Receivable;
import org.wita.erp.domain.entities.transaction.order.dtos.CreateReceivableRequestDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.ReceivableDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.UpdateReceivableRequestDTO;
import org.wita.erp.domain.entities.transaction.order.mappers.ReceivableMapper;
import org.wita.erp.domain.entities.transaction.purchase.mappers.PayableMapper;
import org.wita.erp.domain.repositories.transaction.order.OrderRepository;
import org.wita.erp.domain.repositories.transaction.order.ReceivableRepository;
import org.wita.erp.infra.exceptions.order.OrderException;
import org.wita.erp.infra.exceptions.payable.PayableException;
import org.wita.erp.infra.exceptions.purchase.PurchaseException;
import org.wita.erp.infra.exceptions.receivable.ReceivableException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceivableService {
    private final ReceivableRepository receivableRepository;
    private final OrderRepository orderRepository;
    private final ReceivableMapper receivableMapper;


    public ResponseEntity<Page<ReceivableDTO>> getAllReceivable(Pageable pageable, String searchTerm) {
        Page<Receivable> receivablePage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            receivablePage = receivableRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            receivablePage = receivableRepository.findAll(pageable);
        }

        return ResponseEntity.ok(receivablePage.map(receivableMapper::toDTO));
    }

    public ResponseEntity<List<ReceivableDTO>> save(CreateReceivableRequestDTO data){
        Order order = orderRepository.findById(data.order())
                .orElseThrow(() -> new OrderException("Order not registered in the system", HttpStatus.NOT_FOUND));

        if (order.getPaymentType().getIsImmediate()){
            throw new PayableException("Cannot create receivable for immediate payment orders", HttpStatus.BAD_REQUEST);
        }

        List<Receivable> receivables = new java.util.ArrayList<>(List.of());
        BigDecimal installmentValue = data.value().divide(BigDecimal.valueOf(order.getPaymentType().getMaxInstallments()), 2, RoundingMode.HALF_UP);

        for (int i = 1; i <= order.getPaymentType().getMaxInstallments(); i++) {
            Receivable receivable = new Receivable();
            receivable.setOrder(order);
            receivable.setPaymentStatus(data.paymentStatus());
            receivable.setDueDate(data.dueDate().plusMonths(i));
            receivable.setInstallment(i);
            receivable.setValue(installmentValue);
            receivableRepository.save(receivable);

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

        if (data.order() != null) {
            Order order = orderRepository.findById(data.order())
                    .orElseThrow(() -> new PurchaseException("Order not registered in the system", HttpStatus.NOT_FOUND));
            receivable.setOrder(order);

        }

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
}
