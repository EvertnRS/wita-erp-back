package org.wita.erp.services.transaction.purchase;

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
import org.wita.erp.domain.entities.transaction.purchase.Payable;
import org.wita.erp.domain.entities.transaction.purchase.Purchase;
import org.wita.erp.domain.entities.transaction.purchase.dtos.CreatePayableRequestDTO;
import org.wita.erp.domain.entities.transaction.purchase.dtos.PayableDTO;
import org.wita.erp.domain.entities.transaction.purchase.dtos.UpdatePayableRequestDTO;
import org.wita.erp.domain.entities.transaction.purchase.mappers.PayableMapper;
import org.wita.erp.domain.repositories.transaction.purchase.PayableRepository;
import org.wita.erp.domain.repositories.transaction.purchase.PurchaseRepository;
import org.wita.erp.infra.exceptions.payable.PayableException;
import org.wita.erp.infra.exceptions.purchase.PurchaseException;
import org.wita.erp.services.transaction.purchase.observers.CreatePayablePurchaseObserver;
import org.wita.erp.services.transaction.purchase.observers.PayableCompensationObserver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayableService {
    private final PayableRepository payableRepository;
    private final PurchaseRepository purchaseRepository;
    private final PayableMapper payableMapper;
    private final ApplicationEventPublisher publisher;


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
        LocalDate firstDueDate = LocalDate.now().plusDays(30);
        BigDecimal installmentValue = purchase.getValue().divide(BigDecimal.valueOf(purchase.getInstallments()), 2, RoundingMode.HALF_UP);

        List<Payable> payables = new java.util.ArrayList<>(List.of());

        for (int i = 1; i <= purchase.getInstallments(); i++) {
            Payable payable = new Payable();
            payable.setPurchase(purchase);
            payable.setPaymentStatus(data.paymentStatus());
            payable.setDueDate(firstDueDate.plusDays(30L *i));
            payable.setValue(installmentValue);
            payable.setInstallment(i);
            payableRepository.save(payable);

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

        payableMapper.updatePayableFromDTO(data, payable);
        payableRepository.save(payable);

        return ResponseEntity.ok(payableMapper.toDTO(payable));
    }

    public ResponseEntity<PayableDTO> delete(UUID id) {
        Payable payable = payableRepository.findById(id)
                .orElseThrow(() -> new PayableException("Payable not found", HttpStatus.NOT_FOUND));
        payable.setActive(false);
        payableRepository.save(payable);
        return ResponseEntity.ok(payableMapper.toDTO(payable));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
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
    @Transactional
    public void onPayablePurchaseUpdated(CreatePayablePurchaseObserver event) {
        Purchase purchase = purchaseRepository.findById(event.purchase())
                .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));

        if (purchase.getCompanyPaymentType().getIsImmediate() || !purchase.getCompanyPaymentType().getAllowsInstallments()){
            throw new PayableException("Cannot update payable for this payment method", HttpStatus.BAD_REQUEST);
        }

        List<Payable> payables = payableRepository.findByPurchaseId(event.purchase());

        // Purchase installments updated
        if (purchase.getInstallments() != payables.size()) {
            payables.forEach(
                    payable -> {
                        this.delete(payable.getId());
                    }
            );

            this.save(new CreatePayableRequestDTO(
                    PaymentStatus.PENDING,
                    event.purchase()
            ));
        }

        // Purchase items updated
        if (!purchase.getItems().isEmpty() && !Objects.equals(purchase.getValue(), payables.stream().map(Payable::getValue).reduce(BigDecimal.ZERO, BigDecimal::add))) {
            payables
                    .forEach(payable -> {
                        payable.setValue(purchase.getValue().divide(BigDecimal.valueOf(purchase.getInstallments()), 2, RoundingMode.HALF_UP));
                        payableRepository.save(payable);
                    });
        }
    }
}
