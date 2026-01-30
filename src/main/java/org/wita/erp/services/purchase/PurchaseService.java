package org.wita.erp.services.purchase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.purchase.Purchase;
import org.wita.erp.domain.entities.purchase.dtos.CreatePurchaseRequestDTO;
import org.wita.erp.domain.entities.purchase.dtos.UpdatePurchaseRequestDTO;
import org.wita.erp.domain.entities.purchase.mappers.PurchaseMapper;
import org.wita.erp.domain.entities.supplier.Supplier;
import org.wita.erp.domain.repositories.payment.PaymentTypeRepository;
import org.wita.erp.domain.repositories.purchase.PurchaseRepository;
import org.wita.erp.domain.repositories.supplier.SupplierRepository;
import org.wita.erp.infra.exceptions.payment.PaymentTypeException;
import org.wita.erp.infra.exceptions.purchase.PurchaseException;
import org.wita.erp.infra.exceptions.supplier.SupplierException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final PurchaseMapper purchaseMapper;
    private final SupplierRepository supplierRepository;
    private final PaymentTypeRepository paymentTypeRepository;

    public ResponseEntity<Page<Purchase>> getAllPurchases(Pageable pageable, String searchTerm) {
        Page<Purchase> purchasePage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            purchasePage = purchaseRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            purchasePage = purchaseRepository.findAll(pageable);
        }

        return ResponseEntity.ok(purchasePage);
    }

    public ResponseEntity<Purchase> save(CreatePurchaseRequestDTO data){
        if (purchaseRepository.findByTransactionCode(data.transactionCode()) != null) {
            throw new PurchaseException("Transaction code already exists", HttpStatus.BAD_REQUEST);

        }

        Supplier supplier = supplierRepository.findById(data.supplier())
                .orElseThrow(() -> new SupplierException("Supplier not registered in the system", HttpStatus.NOT_FOUND));

        PaymentType paymentType = paymentTypeRepository.findById(data.paymentType())
                .orElseThrow(() -> new PaymentTypeException("Payment Type not registered in the system", HttpStatus.NOT_FOUND));


        Purchase purchase = new Purchase();
        purchase.setValue(data.value());
        purchase.setSupplier(supplier);
        purchase.setPaymentType(paymentType);
        purchase.setDescription(data.description());
        purchase.setTransactionCode(data.transactionCode());

        purchaseRepository.save(purchase);

        return ResponseEntity.ok(purchase);
    }

    public ResponseEntity<Purchase> update(UUID id, UpdatePurchaseRequestDTO data) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));

        if (data.supplier() != null){
            Supplier supplier = supplierRepository.findById(data.supplier())
                    .orElseThrow(() -> new SupplierException("Supplier not registered in the system", HttpStatus.NOT_FOUND));
            purchase.setSupplier(supplier);
        }

        if (data.paymentType() != null){
            PaymentType paymentType = paymentTypeRepository.findById(data.paymentType())
                    .orElseThrow(() -> new PaymentTypeException("Payment Type not registered in the system", HttpStatus.NOT_FOUND));
            purchase.setPaymentType(paymentType);

        }

        purchaseMapper.updatePurchaseFromDTO(data, purchase);
        purchaseRepository.save(purchase);

        return ResponseEntity.ok(purchase);
    }

    public ResponseEntity<Purchase> delete(UUID id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));
        purchase.setActive(false);
        purchaseRepository.save(purchase);
        return ResponseEntity.ok(purchase);
    }
}
