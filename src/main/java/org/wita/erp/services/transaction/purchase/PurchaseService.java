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
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.company.CompanyPaymentType;
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.entities.stock.MovementReason;
import org.wita.erp.domain.entities.supplier.Supplier;
import org.wita.erp.domain.entities.transaction.purchase.Purchase;
import org.wita.erp.domain.entities.transaction.purchase.PurchaseItem;
import org.wita.erp.domain.entities.transaction.purchase.dtos.*;
import org.wita.erp.domain.entities.transaction.purchase.mappers.PurchaseMapper;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.repositories.payment.PaymentTypeRepository;
import org.wita.erp.domain.repositories.product.ProductRepository;
import org.wita.erp.domain.repositories.stock.MovementReasonRepository;
import org.wita.erp.domain.repositories.stock.StockRepository;
import org.wita.erp.domain.repositories.supplier.SupplierRepository;
import org.wita.erp.domain.repositories.transaction.purchase.PurchaseRepository;
import org.wita.erp.domain.repositories.user.UserRepository;
import org.wita.erp.infra.exceptions.order.OrderException;
import org.wita.erp.infra.exceptions.payment.PaymentTypeException;
import org.wita.erp.infra.exceptions.product.ProductException;
import org.wita.erp.infra.exceptions.purchase.PurchaseException;
import org.wita.erp.infra.exceptions.stock.MovementReasonException;
import org.wita.erp.infra.exceptions.supplier.SupplierException;
import org.wita.erp.infra.exceptions.user.UserException;
import org.wita.erp.services.stock.observers.StockCompensationPurchaseObserver;
import org.wita.erp.services.transaction.purchase.observers.CreatePurchaseObserver;
import org.wita.erp.services.transaction.purchase.observers.UpdatePurchaseObserver;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final PurchaseMapper purchaseMapper;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final PaymentTypeRepository paymentTypeRepository;
    private final MovementReasonRepository movementReasonRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
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

        PaymentType paymentType = paymentTypeRepository.findById(data.paymentType())
                .orElseThrow(() -> new PaymentTypeException("Payment Type not registered in the system", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(data.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

        long uniqueIds = data.products().stream().map(ProductPurchaseRequestDTO::product).distinct().count();
        if (uniqueIds != data.products().size()) {
            throw new PurchaseException("Duplicate products found. Combine quantities.", HttpStatus.BAD_REQUEST);
        }

        Purchase purchase = new Purchase();
        purchase.setDescription(data.description());
        purchase.setTransactionCode(data.transactionCode());
        purchase.setBuyer(buyer);
        purchase.setSupplier(supplier);
        purchase.setPaymentType(paymentType);

        for (ProductPurchaseRequestDTO itemData : data.products()) {
            Product product = productRepository.findById(itemData.product())
                    .orElseThrow(() -> new ProductException("Product " + itemData.product() + " not found", HttpStatus.NOT_FOUND));

            PurchaseItem purchaseItem = createPurchaseItem(product, itemData.quantity());
            purchase.addItem(purchaseItem);
        }

        purchase.calculateSubTotal();
        purchaseRepository.save(purchase);

        publisher.publishEvent(
                new CreatePurchaseObserver(purchase.getId(), movementReason.getId())
        );

        purchaseRepository.save(purchase);

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

        PaymentType paymentType = paymentTypeRepository.findById(data.paymentType())
                .orElseThrow(() -> new PaymentTypeException("Payment Type not registered in the system", HttpStatus.NOT_FOUND));

        Purchase purchase = new Purchase();
        purchase.setValue(data.value());
        purchase.setDescription(data.description());
        purchase.setTransactionCode(data.transactionCode());
        purchase.setBuyer(buyer);
        purchase.setSupplier(supplier);
        purchase.setPaymentType(paymentType);

        purchaseRepository.save(purchase);

        return ResponseEntity.ok(purchaseMapper.toDTO(purchase));
    }

    @Transactional
    public ResponseEntity<PurchaseDTO> update(UUID id, UpdatePurchaseReplacementRequestDTO data) {
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

        if (data.paymentType() != null){
            PaymentType paymentType = paymentTypeRepository.findById(data.paymentType())
                    .orElseThrow(() -> new PaymentTypeException("Payment Type not registered in the system", HttpStatus.NOT_FOUND));

            if (!(paymentType instanceof CompanyPaymentType)) {
                throw new PurchaseException("Payment Type must be of type CompanyPaymentType for purchases", HttpStatus.BAD_REQUEST);
            }
            purchase.setPaymentType(paymentType);
        }

        UpdatePurchaseRequestDTO updateDTO = new UpdatePurchaseRequestDTO(data.transactionCode(), data.description());

        if (!data.products().isEmpty()) {
            long uniqueIds = data.products().stream().map(ProductPurchaseRequestDTO::product).distinct().count();
            if (uniqueIds != data.products().size()) {
                throw new PurchaseException("Duplicate products found. Combine quantities.", HttpStatus.BAD_REQUEST);
            }

            MovementReason movementReason;
            if(data.movementReason() != null){
                movementReason = movementReasonRepository.findById(data.movementReason())
                        .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));
            }
            else{
                UUID productId = data.products().stream()
                        .findFirst()
                        .map(ProductPurchaseRequestDTO::product)
                        .orElseThrow(() ->
                                new OrderException("No products informed", HttpStatus.BAD_REQUEST)
                        );

                movementReason  = stockRepository.findByTransactionIdAndProductId(purchase.getId(), productId).getMovementReason();
            }

            purchase.removeItens();

            for (ProductPurchaseRequestDTO itemData : data.products()) {
                Product product = productRepository.findById(itemData.product())
                        .orElseThrow(() -> new ProductException("Product " + itemData.product() + " not found", HttpStatus.NOT_FOUND));

                PurchaseItem purchaseItem = createPurchaseItem(product, itemData.quantity());
                purchase.addItem(purchaseItem);
            }

            purchase.calculateSubTotal();
            purchaseMapper.updatePurchaseFromDTO(updateDTO, purchase);
            purchaseRepository.save(purchase);
            publisher.publishEvent(
                    new UpdatePurchaseObserver(purchase.getId(), movementReason.getId())
            );
        }

        purchaseMapper.updatePurchaseFromDTO(updateDTO, purchase);
        purchaseRepository.save(purchase);

        return ResponseEntity.ok(purchaseMapper.toDTO(purchase));
    }

    @Transactional
    public ResponseEntity<PurchaseDTO> update(UUID id, UpdatePurchaseExpenseRequestDTO data) {
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

        if (data.paymentType() != null){
            PaymentType paymentType = paymentTypeRepository.findById(data.paymentType())
                    .orElseThrow(() -> new PaymentTypeException("Payment Type not registered in the system", HttpStatus.NOT_FOUND));

            if (!(paymentType instanceof CompanyPaymentType)) {
                throw new PurchaseException("Payment Type must be of type CompanyPaymentType for purchases", HttpStatus.BAD_REQUEST);
            }
            purchase.setPaymentType(paymentType);
        }

        if (data.value() != null){
            purchase.setValue(data.value());
        }

        UpdatePurchaseRequestDTO updateDTO = new UpdatePurchaseRequestDTO(data.transactionCode(), data.description());

        purchaseMapper.updatePurchaseFromDTO(updateDTO, purchase);
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

    /*@Transactional
    public ResponseEntity<PurchaseDTO> addProductInPurchase(UUID purchaseId, ProductPurchaseRequestDTO data) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));
        Product product = productRepository.findById(data.product())
                .orElseThrow(() -> new ProductException("Product " + data.product() + " not found", HttpStatus.NOT_FOUND));

        PurchaseItem purchaseItem = createPurchaseItem(product, data.quantity());
        purchase.addItem(purchaseItem);

        purchaseRepository.save(purchase);
        return ResponseEntity.ok(purchaseMapper.toDTO(purchase));
    }*/

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

}
