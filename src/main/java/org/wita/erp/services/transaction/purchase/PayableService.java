package org.wita.erp.services.transaction.purchase;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.wita.erp.domain.entities.audit.EntityType;
import org.wita.erp.domain.entities.transaction.PaymentStatus;
import org.wita.erp.domain.entities.transaction.purchase.Payable;
import org.wita.erp.domain.entities.transaction.purchase.Purchase;
import org.wita.erp.domain.entities.transaction.purchase.dtos.CreatePayableRequestDTO;
import org.wita.erp.domain.entities.transaction.purchase.dtos.DeletePayableRequestDTO;
import org.wita.erp.domain.entities.transaction.purchase.dtos.PayableDTO;
import org.wita.erp.domain.entities.transaction.purchase.dtos.UpdatePayableRequestDTO;
import org.wita.erp.domain.entities.transaction.purchase.mappers.PayableMapper;
import org.wita.erp.domain.repositories.transaction.purchase.PayableRepository;
import org.wita.erp.domain.repositories.transaction.purchase.PurchaseRepository;
import org.wita.erp.infra.exceptions.payable.PayableException;
import org.wita.erp.infra.exceptions.purchase.PurchaseException;
import org.wita.erp.infra.schedules.handler.ScheduledTaskTypes;
import org.wita.erp.infra.schedules.scheduler.SchedulerService;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;
import org.wita.erp.services.transaction.purchase.observers.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayableService {
    private final PayableRepository payableRepository;
    private final PurchaseRepository purchaseRepository;
    private final PayableMapper payableMapper;
    private final SchedulerService schedulerService;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public ResponseEntity<Page<PayableDTO>> getAllPayable(Pageable pageable, String searchTerm) {
        Page<Payable> payablePage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            payablePage = payableRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            payablePage = payableRepository.findAll(pageable);
        }

        return ResponseEntity.ok(payablePage.map(payableMapper::toDTO));
    }

    public ResponseEntity<List<PayableDTO>> save(CreatePayableRequestDTO data){
        Purchase purchase = purchaseRepository.findById(data.purchase())
                .orElseThrow(() -> new PurchaseException("Purchase not registered in the system", HttpStatus.NOT_FOUND));

        if (purchase.getCompanyPaymentType().getIsImmediate()){
            throw new PayableException("Cannot create payable for immediate payment purchases", HttpStatus.BAD_REQUEST);
        }
        BigDecimal installmentValue = purchase.getValue().divide(BigDecimal.valueOf(purchase.getInstallments()), 2, RoundingMode.HALF_UP);
        int closingDay = purchase.getCompanyPaymentType().getClosingDay();
        LocalDate baseDate = LocalDate.now();

        List<Payable> payables = new java.util.ArrayList<>(List.of());

        for (int i = 1; i <= purchase.getInstallments(); i++) {
            LocalDate installmentMonth = baseDate.plusMonths(i);
            int safeDay = Math.min(closingDay, installmentMonth.lengthOfMonth());
            LocalDate dueDate = installmentMonth.withDayOfMonth(safeDay);

            Payable payable = new Payable();
            payable.setPurchase(purchase);
            payable.setPaymentStatus(data.paymentStatus());
            payable.setDueDate(dueDate);
            payable.setValue(installmentValue);
            payable.setInstallment(i);

            payableRepository.save(payable);

            schedulerService.schedule(
                    ScheduledTaskTypes.PAYABLE_DUE_SOON,
                    payable.getId().toString(),
                    payable.getDueDate().minusDays(3).atStartOfDay()
            );

            schedulerService.schedule(
                    ScheduledTaskTypes.PAYABLE_OVERDUE,
                    payable.getId().toString(),
                    payable.getDueDate().atStartOfDay()
            );

            payables.add(payable);
        }

        List<PayableDTO> dtos = payables.stream()
                .map(payableMapper::toDTO)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    public ResponseEntity<PayableDTO> update(UUID id, UpdatePayableRequestDTO data) {
        Payable payable = payableRepository.findById(id)
                .orElseThrow(() -> new PayableException("Payable not found", HttpStatus.NOT_FOUND));

        PaymentStatus currentStatus = payable.getPaymentStatus();
        PaymentStatus newStatus = data.paymentStatus();
        LocalDate newDueDate = data.dueDate();

        if(!currentStatus.allowsManualUpdate()){
            throw new PayableException("Payment status cannot be updated manually", HttpStatus.BAD_REQUEST);
        }

        if(newStatus == PaymentStatus.PENDING || newStatus == PaymentStatus.OVERDUE){
            throw new PayableException("This new payment status cannot be set manually", HttpStatus.BAD_REQUEST);

        }

        boolean statusChanging = newStatus != null && newStatus != currentStatus;
        boolean dueDateChanging = newDueDate != null;

        if (dueDateChanging && currentStatus != PaymentStatus.PENDING) {
            throw new PayableException(
                    "Due date can only be changed while payable is PENDING",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (statusChanging && currentStatus == PaymentStatus.PENDING) {
            cancelScheduleTasks(id);
        }

        if (!statusChanging && dueDateChanging) {
            rescheduleTasks(id, newDueDate);
        }

        if(newStatus == PaymentStatus.PAID){
            payable.setPaidAt(LocalDateTime.now());

            List<PaymentStatus> statuses =
                    payableRepository.findDistinctStatusesByPurchaseId(payable.getPurchase().getId());

            if(statuses.size() == 1 && statuses.contains(PaymentStatus.PAID)){
                publisher.publishEvent(new PayableStatusChangedObserver(payable.getPurchase().getId(), PaymentStatus.PAID));
            }
        }

        payableMapper.updatePayableFromDTO(data, payable);
        payableRepository.save(payable);

        return ResponseEntity.ok(payableMapper.toDTO(payable));
    }

    public ResponseEntity<PayableDTO> delete(UUID id, DeletePayableRequestDTO data) {
        Payable payable = payableRepository.findById(id)
                .orElseThrow(() -> new PayableException("Payable not found", HttpStatus.NOT_FOUND));
        payable.setActive(false);

        payableRepository.save(payable);

        if(payable.getPaymentStatus() == PaymentStatus.PENDING){
            this.cancelScheduleTasks(id);
        }
        this.auditPayableSoftDelete(id, data.reason());

        return ResponseEntity.ok(payableMapper.toDTO(payable));
    }

    @EventListener
    public void onPurchaseSoftDelete(PurchaseSoftDeleteObserver event) {
        List<UUID> payableIds = payableRepository.cascadeDeleteFromPurchase(event.purchase());
        if(!payableIds.isEmpty()){
            for (UUID payableId : payableIds) {
                this.auditPayableSoftDelete(payableId, "Cascade delete from purchase " + event.purchase());

                cancelScheduleTasks(payableId);
            }
        }
    }

    @Async
    public void auditPayableSoftDelete(UUID id, String reason){
        publisher.publishEvent(new SoftDeleteLogObserver(id.toString(), EntityType.PAYABLE.getEntityType(), reason));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onPayablePurchaseCreated(CreatePayablePurchaseObserver event) {
        try{
            purchaseRepository.findById(event.purchase())
                    .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));

            CreatePayableRequestDTO dto = new CreatePayableRequestDTO(
                    PaymentStatus.PENDING,
                    event.purchase()
            );

            this.save(dto);

        } catch (Exception e) {
            publisher.publishEvent(new PayableCompensationObserver(event.purchase()));
            throw new PayableException("Failed to process stock movements for purchase: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPayablePurchaseUpdated(UpdatePayablePurchaseObserver event) {
        Purchase purchase = purchaseRepository.findById(event.purchase())
                .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));

        if (purchase.getCompanyPaymentType().getIsImmediate() || !purchase.getCompanyPaymentType().getAllowsInstallments()){
            throw new PayableException("Cannot update payable for this payment method", HttpStatus.BAD_REQUEST);
        }

        List<Payable> payables = payableRepository.findByPurchaseId(event.purchase());

        synchronizeInstallments(purchase, payables);

        synchronizeValues(purchase, payables);
    }

    private void synchronizeInstallments(Purchase purchase, List<Payable> payables) {
        if (purchase.getInstallments() != payables.size()) {
            payables.forEach(payable ->
                    this.delete(
                            payable.getId(),
                            new DeletePayableRequestDTO("Purchases installments updated")
                    )
            );

            this.save(new CreatePayableRequestDTO(
                    PaymentStatus.PENDING,
                    purchase.getId()
            ));
        }
    }

    private void synchronizeValues(Purchase purchase, List<Payable> payables) {
        BigDecimal totalPayables = payables.stream()
                .map(Payable::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (!purchase.getItems().isEmpty()
                && !Objects.equals(purchase.getValue(), totalPayables)) {

            BigDecimal installmentValue =
                    purchase.getValue().divide(
                            BigDecimal.valueOf(purchase.getInstallments()),
                            2,
                            RoundingMode.HALF_UP
                    );

            payables.forEach(payable -> {
                payable.setValue(installmentValue);
                payableRepository.save(payable);
            });
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPurchaseStatusChanged(PurchaseStatusChangedObserver event) {
        purchaseRepository.findById(event.purchase())
                .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));

        List<Payable> payables = payableRepository.findByPurchaseId(event.purchase());

        switch (event.newStatus()) {

            case CANCELED -> cancelAllPayables(payables);
            case REFUNDED -> refundOrCancelPayables(payables);
        }
    }

    private void cancelAllPayables(List<Payable> payables) {
        payables.forEach(payable -> {
            payable.setPaymentStatus(PaymentStatus.CANCELED);
            payableRepository.save(payable);
            cancelScheduleTasks(payable.getId());
        });
    }

    private void refundOrCancelPayables(List<Payable> payables) {
        payables.forEach(payable -> {

            if (payable.getPaymentStatus() == PaymentStatus.PAID) {
                payable.setPaymentStatus(PaymentStatus.REFUNDED);
            } else {
                payable.setPaymentStatus(PaymentStatus.CANCELED);
            }

            payableRepository.save(payable);
            cancelScheduleTasks(payable.getId());
        });
    }

    private void rescheduleTasks(UUID id, LocalDate dueDate) {
        schedulerService.reschedule(
                ScheduledTaskTypes.PAYABLE_OVERDUE,
                id.toString(),
                dueDate.atStartOfDay()
        );

        if (LocalDate.now().isBefore(dueDate.minusDays(3))) {
            schedulerService.reschedule(
                    ScheduledTaskTypes.PAYABLE_DUE_SOON,
                    id.toString(),
                    dueDate.minusDays(3).atStartOfDay()
            );
        }
    }

    public void cancelScheduleTasks(UUID id){
        schedulerService.cancel(ScheduledTaskTypes.PAYABLE_DUE_SOON, id.toString());
        schedulerService.cancel(ScheduledTaskTypes.PAYABLE_OVERDUE, id.toString());
    }
}
