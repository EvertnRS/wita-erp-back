package org.wita.erp.services.transaction.purchase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.payment.company.CompanyPaymentType;
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.entities.stock.MovementReason;
import org.wita.erp.domain.entities.supplier.Supplier;
import org.wita.erp.domain.entities.transaction.dtos.PurchaseDTO;
import org.wita.erp.domain.entities.transaction.purchase.Purchase;
import org.wita.erp.domain.entities.transaction.purchase.PurchaseItem;
import org.wita.erp.domain.entities.transaction.purchase.dtos.*;
import org.wita.erp.domain.entities.transaction.purchase.mappers.PurchaseMapper;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.repositories.payment.company.CompanyPaymentTypeRepository;
import org.wita.erp.domain.repositories.product.ProductRepository;
import org.wita.erp.domain.repositories.stock.MovementReasonRepository;
import org.wita.erp.domain.repositories.supplier.SupplierRepository;
import org.wita.erp.domain.repositories.transaction.purchase.PurchaseRepository;
import org.wita.erp.domain.repositories.user.UserRepository;
import org.wita.erp.infra.exceptions.payment.PaymentTypeException;
import org.wita.erp.infra.exceptions.product.ProductException;
import org.wita.erp.infra.exceptions.purchase.PurchaseException;
import org.wita.erp.infra.exceptions.stock.MovementReasonException;
import org.wita.erp.infra.exceptions.supplier.SupplierException;
import org.wita.erp.infra.exceptions.user.UserException;
import org.wita.erp.services.stock.observers.StockCompensationPurchaseObserver;
import org.wita.erp.services.transaction.purchase.observers.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final PurchaseMapper purchaseMapper;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final CompanyPaymentTypeRepository companyPaymentTypeRepository;
    private final MovementReasonRepository movementReasonRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;

    public ResponseEntity<Page<PurchaseDTO>> getAllPurchases(Pageable pageable, String searchTerm) {
        Page<Purchase> purchasePage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            purchasePage = purchaseRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            purchasePage = purchaseRepository.findAll(pageable);
        }

        return ResponseEntity.ok(purchasePage.map(purchaseMapper::toDTO));
    }

    @Transactional
    public ResponseEntity<PurchaseDTO> save(CreateReplacementPurchaseRequestDTO data){
        if (purchaseRepository.findByTransactionCode(data.transactionCode()) != null) {
            throw new PurchaseException("Transaction code already exists", HttpStatus.BAD_REQUEST);
        }

        User buyer = userRepository.findById(data.buyer())
                .orElseThrow(() -> new PurchaseException("Buyer not found", HttpStatus.NOT_FOUND));

        Supplier supplier = supplierRepository.findById(data.supplier())
                .orElseThrow(() -> new SupplierException("Supplier not registered in the system", HttpStatus.NOT_FOUND));

        CompanyPaymentType companyPaymentType = companyPaymentTypeRepository.findById(data.companyPaymentType())
                .orElseThrow(() -> new PaymentTypeException("Payment Type not registered in the system", HttpStatus.NOT_FOUND));

        if((!companyPaymentType.getAllowsInstallments() || companyPaymentType.getIsImmediate()) && data.installments() != null){
            throw new PurchaseException("This payment type does not allow installments", HttpStatus.BAD_REQUEST);
        }

        MovementReason movementReason = movementReasonRepository.findById(data.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

        long uniqueIds = data.products().stream().map(ProductPurchaseRequestDTO::productId).distinct().count();
        if (uniqueIds != data.products().size()) {
            throw new PurchaseException("Duplicate products found. Combine quantities.", HttpStatus.BAD_REQUEST);
        }

        Purchase purchase = new Purchase();
        purchase.setInstallments(data.installments());
        purchase.setDescription(data.description());
        purchase.setTransactionCode(data.transactionCode());
        purchase.setBuyer(buyer);
        purchase.setSupplier(supplier);
        purchase.setCompanyPaymentType(companyPaymentType);

        for (ProductPurchaseRequestDTO itemData : data.products()) {
            Product product = productRepository.findById(itemData.productId())
                    .orElseThrow(() -> new ProductException("Product " + itemData.productId() + " not found", HttpStatus.NOT_FOUND));

            PurchaseItem purchaseItem = createPurchaseItem(product, itemData.quantity());
            purchase.addItem(purchaseItem);
        }

        purchase.calculateSubTotal();

        if (data.installments() != null) {
            publisher.publishEvent(
                    new CreatePayablePurchaseObserver(purchase.getId())
            );
        }

        purchaseRepository.save(purchase);

        publisher.publishEvent(
                new CreatePurchaseObserver(purchase.getId(), movementReason.getId())
        );


        return ResponseEntity.ok(purchaseMapper.toDTO(purchase));
    }

    @Transactional
    public ResponseEntity<PurchaseDTO> save(CreateExpensePurchaseRequestDTO data){
        if (purchaseRepository.findByTransactionCode(data.transactionCode()) != null) {
            throw new PurchaseException("Transaction code already exists", HttpStatus.BAD_REQUEST);
        }

        User buyer = userRepository.findById(data.buyer())
                .orElseThrow(() -> new PurchaseException("Buyer not found", HttpStatus.NOT_FOUND));

        Supplier supplier = supplierRepository.findById(data.supplier())
                .orElseThrow(() -> new SupplierException("Supplier not registered in the system", HttpStatus.NOT_FOUND));

        CompanyPaymentType companyPaymentType = companyPaymentTypeRepository.findById(data.companyPaymentType())
                .orElseThrow(() -> new PaymentTypeException("Payment Type not registered in the system", HttpStatus.NOT_FOUND));

        if((!companyPaymentType.getAllowsInstallments() || companyPaymentType.getIsImmediate()) && data.installments() != null){
            throw new PurchaseException("This payment type does not allow installments", HttpStatus.BAD_REQUEST);
        }

        Purchase purchase = new Purchase();
        purchase.setInstallments(data.installments());
        purchase.setValue(data.value());
        purchase.setDescription(data.description());
        purchase.setTransactionCode(data.transactionCode());
        purchase.setBuyer(buyer);
        purchase.setSupplier(supplier);
        purchase.setCompanyPaymentType(companyPaymentType);

        if (data.installments() != null) {
            publisher.publishEvent(
                    new CreatePayablePurchaseObserver(purchase.getId())
            );
        }

        purchaseRepository.save(purchase);

        return ResponseEntity.ok(purchaseMapper.toDTO(purchase));
    }

    @Transactional
    public ResponseEntity<PurchaseDTO> update(UUID id, UpdatePurchaseRequestDTO data) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));

        if (purchaseRepository.findByTransactionCode(data.transactionCode()) != null) {
            throw new PurchaseException("Transaction code already exists", HttpStatus.BAD_REQUEST);
        }

        if(data.buyer() != null){
            User buyer = userRepository.findById(data.buyer())
                    .orElseThrow(() -> new UserException("Buyer not found", HttpStatus.NOT_FOUND));
            purchase.setBuyer(buyer);

        }

        if (data.supplier() != null){
            Supplier supplier = supplierRepository.findById(data.supplier())
                    .orElseThrow(() -> new SupplierException("Supplier not registered in the system", HttpStatus.NOT_FOUND));
            purchase.setSupplier(supplier);
        }

        if (data.companyPaymentType() != null){
            CompanyPaymentType companyPaymentType = companyPaymentTypeRepository.findById(data.companyPaymentType())
                    .orElseThrow(() -> new PaymentTypeException("Payment Type not registered in the system", HttpStatus.NOT_FOUND));

            if((!companyPaymentType.getAllowsInstallments() || companyPaymentType.getIsImmediate()) && data.installments() != null){
                throw new PurchaseException("This payment type does not allow installments", HttpStatus.BAD_REQUEST);
            }

            purchase.setCompanyPaymentType(companyPaymentType);
        }

        if (purchase.getItems().isEmpty()){
            if (data.value() != null){
                purchase.setValue(data.value());
            }
             else {
                throw new PurchaseException("Value is only updatable for expense purchases", HttpStatus.BAD_REQUEST);
            }
        }

        purchaseMapper.updatePurchaseFromDTO(data, purchase);

        if(purchase.getInstallments() != null){
            publisher.publishEvent(new UpdatePayablePurchaseObserver(purchase.getId()));
        }

        purchaseRepository.save(purchase);

        return ResponseEntity.ok(purchaseMapper.toDTO(purchase));
    }

    public ResponseEntity<PurchaseDTO> delete(UUID id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));
        purchase.setActive(false);
        purchaseRepository.save(purchase);
        return ResponseEntity.ok(purchaseMapper.toDTO(purchase));
    }

    @Transactional
    public ResponseEntity<PurchaseDTO> addProductInPurchase(UUID purchaseId, ProductInPurchaseDTO data) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));

        Product product = productRepository.findById(data.product().productId())
                .orElseThrow(() -> new ProductException("Product " + data.product().productId() + " not found", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(data.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

        PurchaseItem currentItem;

        Optional<PurchaseItem> existingItemOpt = purchase.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(data.product().productId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            currentItem = existingItemOpt.get();

            int updatedQuantity = currentItem.getQuantity() + data.product().quantity();
            currentItem.setQuantity(updatedQuantity);
        } else {
            currentItem = createPurchaseItem(product, data.product().quantity());
            purchase.addItem(currentItem);
        }
        purchase.calculateSubTotal();

        publisher.publishEvent(new AddProductInPurchaseObserver(purchase.getId(), movementReason.getId(), data.product()));

        if(purchase.getInstallments() != null){
            publisher.publishEvent(new UpdatePayablePurchaseObserver(purchase.getId()));
        }

        purchaseRepository.save(purchase);

        return ResponseEntity.ok(purchaseMapper.toDTO(purchase));
    }

    @Transactional
    public ResponseEntity<PurchaseDTO> removeProductInPurchase(UUID purchaseId, ProductInPurchaseDTO data) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));

        productRepository.findById(data.product().productId())
                .orElseThrow(() -> new ProductException("Product " + data.product().productId() + " not found", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(data.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

        PurchaseItem currentItem = purchase.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(data.product().productId()))
                .findFirst()
                .orElseThrow(() -> new PurchaseException("Product not found in this purchase", HttpStatus.NOT_FOUND));

        if (currentItem.getQuantity() < data.product().quantity()) {
            throw new PurchaseException("Quantity requested for removal exceeds the available quantity.", HttpStatus.BAD_REQUEST);
        }

        else if (currentItem.getQuantity().equals(data.product().quantity())) {
            purchase.getItems().remove(currentItem);
        } else {
            int newQuantity = currentItem.getQuantity() - data.product().quantity();
            currentItem.setQuantity(newQuantity);
        }

        purchase.calculateSubTotal();

        publisher.publishEvent(new RemoveProductInPurchaseObserver(purchase.getId(), movementReason.getId(), data.product()));

        if(purchase.getInstallments() != null){
            publisher.publishEvent(new UpdatePayablePurchaseObserver(purchase.getId()));
        }

        purchaseRepository.save(purchase);

        return ResponseEntity.ok(purchaseMapper.toDTO(purchase));
    }

    private PurchaseItem createPurchaseItem(Product product, int quantity) {
        if ((product.getQuantityInStock() - quantity) <= product.getMinQuantity()){
            throw new ProductException("Not enough stock for product: " + product.getName(), HttpStatus.BAD_REQUEST);
        }
        PurchaseItem purchaseItem = new PurchaseItem();
        purchaseItem.setProduct(product);
        purchaseItem.setQuantity(quantity);
        purchaseItem.setUnitPrice(product.getPrice());
        purchaseItem.setTotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return purchaseItem;
    }

    @EventListener
    @Async
    public void onStockCompensationPurchase(StockCompensationPurchaseObserver event) {
        purchaseRepository.findById(event.purchase())
                .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));

        this.delete(event.purchase());
    }

    @EventListener
    @Async
    public void onPayableCompensationPurchase(PayableCompensationObserver event) {
        purchaseRepository.findById(event.purchase())
                .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));

        this.delete(event.purchase());
    }

}
