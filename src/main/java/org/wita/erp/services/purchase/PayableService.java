package org.wita.erp.services.purchase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.purchase.Payable;
import org.wita.erp.domain.entities.purchase.Purchase;
import org.wita.erp.domain.entities.purchase.dtos.CreatePayableRequestDTO;
import org.wita.erp.domain.entities.purchase.dtos.UpdatePayableRequestDTO;
import org.wita.erp.domain.entities.purchase.mappers.PayableMapper;
import org.wita.erp.domain.repositories.purchase.PayableRepository;
import org.wita.erp.domain.repositories.purchase.PurchaseRepository;
import org.wita.erp.infra.exceptions.payable.PayableException;
import org.wita.erp.infra.exceptions.purchase.PurchaseException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayableService {
    private final PayableRepository payableRepository;
    private final PurchaseRepository purchaseRepository;
    private final PayableMapper payableMapper;


    public ResponseEntity<Page<Payable>> getAllPayable(Pageable pageable, String searchTerm) {
        Page<Payable> payablePage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            payablePage = payableRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            payablePage = payableRepository.findAll(pageable);
        }

        return ResponseEntity.ok(payablePage);
    }

    public ResponseEntity<Payable> save(CreatePayableRequestDTO data){
        Purchase purchase = purchaseRepository.findById(data.purchase())
                .orElseThrow(() -> new PurchaseException("Purchase not registered in the system", HttpStatus.NOT_FOUND));

        Payable payable = new Payable();
        payable.setPurchase(purchase);
        payable.setPaymentStatus(data.paymentStatus());
        payable.setDueDate(data.dueDate());

        payableRepository.save(payable);

        return ResponseEntity.ok(payable);
    }

    public ResponseEntity<Payable> update(UUID id, UpdatePayableRequestDTO data) {
        Payable payable = payableRepository.findById(id)
                .orElseThrow(() -> new PayableException("Payable not found", HttpStatus.NOT_FOUND));

        if (data.purchase() != null) {
            Purchase purchase = purchaseRepository.findById(data.purchase())
                    .orElseThrow(() -> new PurchaseException("Purchase not registered in the system", HttpStatus.NOT_FOUND));
            payable.setPurchase(purchase);

        }

        payableMapper.updatePayableFromDTO(data, payable);
        payableRepository.save(payable);

        return ResponseEntity.ok(payable);
    }

    public ResponseEntity<Payable> delete(UUID id) {
        Payable payable = payableRepository.findById(id)
                .orElseThrow(() -> new PayableException("Payable not found", HttpStatus.NOT_FOUND));
        payable.setActive(false);
        payableRepository.save(payable);
        return ResponseEntity.ok(payable);
    }
}
