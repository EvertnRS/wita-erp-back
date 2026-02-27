package org.wita.erp.services.stock;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.entities.stock.MovementReason;
import org.wita.erp.domain.entities.stock.StockMovement;
import org.wita.erp.domain.entities.stock.StockMovementType;
import org.wita.erp.domain.entities.stock.dtos.CreateStockRequestDTO;
import org.wita.erp.domain.entities.stock.dtos.DeleteStockRequestDTO;
import org.wita.erp.domain.entities.stock.dtos.StockMovementDTO;
import org.wita.erp.domain.entities.stock.dtos.UpdateStockRequestDTO;
import org.wita.erp.domain.entities.stock.mappers.StockMapper;
import org.wita.erp.domain.entities.transaction.Transaction;
import org.wita.erp.domain.entities.transaction.order.Order;
import org.wita.erp.domain.entities.transaction.purchase.Purchase;
import org.wita.erp.domain.repositories.product.ProductRepository;
import org.wita.erp.domain.repositories.stock.MovementReasonRepository;
import org.wita.erp.domain.repositories.stock.StockRepository;
import org.wita.erp.domain.repositories.transaction.TransactionRepository;
import org.wita.erp.domain.repositories.transaction.order.OrderRepository;
import org.wita.erp.domain.repositories.transaction.purchase.PurchaseRepository;
import org.wita.erp.infra.exceptions.order.OrderException;
import org.wita.erp.infra.exceptions.product.ProductException;
import org.wita.erp.infra.exceptions.purchase.PurchaseException;
import org.wita.erp.infra.exceptions.stock.MovementReasonException;
import org.wita.erp.infra.exceptions.stock.StockException;
import org.wita.erp.infra.exceptions.transaction.TransactionException;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;
import org.wita.erp.services.product.observers.ProductSoftDeleteObserver;
import org.wita.erp.services.stock.observers.*;
import org.wita.erp.services.transaction.order.observers.*;
import org.wita.erp.services.transaction.purchase.observers.*;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final MovementReasonRepository movementReasonRepository;
    private final StockMapper stockMapper;
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;
    private final PurchaseRepository purchaseRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public ResponseEntity<Page<StockMovementDTO>> getAllStock(Pageable pageable, String searchTerm) {
        Page<StockMovement> stockPage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            stockPage = stockRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            stockPage = stockRepository.findAll(pageable);
        }

        Page<StockMovementDTO> dto = stockPage.map(stockMapper::StockMovementToDTO);

        return ResponseEntity.ok(dto);
    }

    @Transactional
    public ResponseEntity<StockMovementDTO> save(CreateStockRequestDTO data) {
        Product product = productRepository.findById(data.product())
                .orElseThrow(() -> new ProductException("Product not registered in the system", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(data.movementReason())
                .orElseThrow(() -> new MovementReasonException("MovementReason not registered in the system", HttpStatus.NOT_FOUND));

        Transaction transaction = transactionRepository.findById(data.transaction())
                .orElseThrow(() -> new TransactionException("Transaction not registered in the system", HttpStatus.NOT_FOUND));

        StockMovement stock = new StockMovement();
        stock.setStockMovementType(data.movementType());

        if (transaction instanceof Order order) {
            stock.setTransaction(order);
        }

        if (transaction instanceof Purchase purchase){
            stock.setTransaction(purchase);
        }

        publisher.publishEvent(new StockMovementObserver(stock.getStockMovementType(), product.getId(), data.quantity()));

        stock.setProduct(product);
        stock.setQuantity(data.quantity());
        stock.setMovementReason(movementReason);

        stockRepository.save(stock);

        return ResponseEntity.ok(stockMapper.StockMovementToDTO(stock));
    }

    public ResponseEntity<StockMovementDTO> update(UUID id, UpdateStockRequestDTO data) {
        StockMovement stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockException("Stock not found", HttpStatus.NOT_FOUND));

        if (data.product() != null) {
            Product product = productRepository.findById(data.product())
                    .orElseThrow(() -> new ProductException("Product not registered in the system", HttpStatus.NOT_FOUND));
            stock.setProduct(product);

        }

        if (data.transaction() != null){
            Transaction transaction = transactionRepository.findById(data.transaction())
                    .orElseThrow(() -> new TransactionException("Transaction not registered in the system", HttpStatus.NOT_FOUND));


            if (transaction instanceof Order) {
                Order order = orderRepository.findById(data.transaction())
                        .orElseThrow(() -> new OrderException("Order not registered in the system", HttpStatus.NOT_FOUND));

                stock.setStockMovementType(StockMovementType.OUT);
                stock.setTransaction(order);
            }

            if (transaction instanceof Purchase){
                Purchase purchase = purchaseRepository.findById(data.transaction())
                        .orElseThrow(() -> new PurchaseException("Purchase not registered in the system", HttpStatus.NOT_FOUND));

                stock.setStockMovementType(StockMovementType.IN);
                stock.setTransaction(purchase);
            }
        }

        publisher.publishEvent(new UpdateStockMovementObserver(stock.getStockMovementType(), data.product(), data.quantity()));

        if (data.movementReason() != null) {
            MovementReason movementReason = movementReasonRepository.findById(data.movementReason())
                    .orElseThrow(() -> new MovementReasonException("MovementReason not registered in the system", HttpStatus.NOT_FOUND));
            stock.setMovementReason(movementReason);
        }

        stockMapper.updateStockFromDTO(data, stock);
        stockRepository.save(stock);

        return ResponseEntity.ok(stockMapper.StockMovementToDTO(stock));
    }

    public ResponseEntity<StockMovementDTO> delete(UUID id, DeleteStockRequestDTO data) {
        StockMovement stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockException("Stock not found", HttpStatus.NOT_FOUND));
        stock.setActive(false);
        stockRepository.save(stock);

        this.auditStockSoftDelete(id, data.reason());

        return ResponseEntity.ok(stockMapper.StockMovementToDTO(stock));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void onOrderCreated(CreateOrderObserver event) {
        try{
            Order order = orderRepository
                    .findByIdWithItems(event.order())
                    .orElseThrow(() ->
                            new PurchaseException("Purchase not found after save",
                                    HttpStatus.INTERNAL_SERVER_ERROR)
                    );

            MovementReason movementReason = movementReasonRepository.findById(event.movementReason())
                    .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

            order.getItems().forEach(orderItem -> {
                CreateStockRequestDTO dto = new CreateStockRequestDTO(orderItem.getProduct().getId(), orderItem.getQuantity(), movementReason.getId(), order.getId(), StockMovementType.OUT);
                this.save(dto);
            });

        } catch (Exception e) {
            publisher.publishEvent(new StockCompensationOrderObserver(event.order()));
            throw new StockException("Failed to process stock movements for order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void onPurchaseCreated(CreatePurchaseObserver event) {
        try{
            Purchase purchase = purchaseRepository
                    .findByIdWithItems(event.purchase())
                    .orElseThrow(() ->
                            new PurchaseException("Purchase not found",
                                    HttpStatus.NOT_FOUND)
                    );

            MovementReason movementReason = movementReasonRepository.findById(event.movementReason())
                    .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

            purchase.getItems().forEach(purchaseItem -> {
                CreateStockRequestDTO dto = new CreateStockRequestDTO(purchaseItem.getProduct().getId(), purchaseItem.getQuantity(), movementReason.getId(), purchase.getId(), StockMovementType.IN);
                this.save(dto);
            });

        } catch (Exception e) {
            publisher.publishEvent(new StockCompensationPurchaseObserver(event.purchase()));
            throw new StockException("Failed to process stock movements for order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onOrderUpdated(UpdateOrderObserver event) {
        Order order = orderRepository.findById(event.order())
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(event.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

        order.getItems().forEach(orderItem -> {
            StockMovement stockMovement = stockRepository.findByTransactionIdAndProductId(order.getId(), orderItem.getProduct().getId());
            UpdateStockRequestDTO dto = new UpdateStockRequestDTO(orderItem.getProduct().getId(), orderItem.getQuantity(), movementReason.getId(), order.getId());
            this.update(stockMovement.getId(), dto);
        });
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPurchaseUpdated(UpdatePurchaseObserver event) {

        Purchase purchase = purchaseRepository.findById(event.purchase())
                .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(event.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

        purchase.getItems().forEach(purchaseItem -> {
            StockMovement stockMovement = stockRepository.findByTransactionIdAndProductId(purchase.getId(), purchaseItem.getProduct().getId());
            UpdateStockRequestDTO dto = new UpdateStockRequestDTO(purchaseItem.getProduct().getId(), purchaseItem.getQuantity(), movementReason.getId(), purchase.getId());
            this.update(stockMovement.getId(), dto);
        });
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onAddItemInOrder(AddProductInOrderObserver event) {
        Order order = orderRepository.findById(event.order())
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(event.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

        CreateStockRequestDTO dto = new CreateStockRequestDTO(
                event.stockDifference().productId(),
                event.stockDifference().quantity(),
                movementReason.getId(),
                order.getId(),
                StockMovementType.OUT);

        this.save(dto);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onRemoveItemInOrder(RemoveProductInOrderObserver event) {
        Order order = orderRepository.findById(event.order())
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(event.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

        CreateStockRequestDTO dto = new CreateStockRequestDTO(
                event.stockDifference().productId(),
                event.stockDifference().quantity(),
                movementReason.getId(),
                order.getId(),
                StockMovementType.IN);

        this.save(dto);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onAddItemInPurchase(AddProductInPurchaseObserver event) {
        Purchase purchase = purchaseRepository.findById(event.purchase())
                .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(event.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

        CreateStockRequestDTO dto = new CreateStockRequestDTO(
                event.stockDifference().productId(),
                event.stockDifference().quantity(),
                movementReason.getId(),
                purchase.getId(),
                StockMovementType.IN);

        this.save(dto);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onRemoveItemInPurchase(RemoveProductInPurchaseObserver event) {
        Purchase purchase = purchaseRepository.findById(event.purchase())
                .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(event.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

        CreateStockRequestDTO dto = new CreateStockRequestDTO(
                event.stockDifference().productId(),
                event.stockDifference().quantity(),
                movementReason.getId(),
                purchase.getId(),
                StockMovementType.OUT);

        this.save(dto);
    }

    @EventListener
    public void onProductSoftDelete(ProductSoftDeleteObserver event) {
        List<UUID> stockIds = stockRepository.cascadeDeleteFromProduct(event.product());
        if(!stockIds.isEmpty()){
            for (UUID stockId : stockIds) {
                this.auditStockSoftDelete(stockId, "Cascade delete from product " + event.product());
            }
        }
    }

    @EventListener
    public void onMovementReasonSoftDelete(MovementReasonSoftDeleteObserver event) {
        List<UUID> stockIds = stockRepository.cascadeDeleteFromMovementReason(event.movementReason());
        if(!stockIds.isEmpty()){
            for (UUID stockId : stockIds) {
                this.auditStockSoftDelete(stockId, "Cascade delete from movement reason " + event.movementReason());
            }
        }
    }

    @EventListener
    public void onOrderSoftDelete(OrderSoftDeleteObserver event) {
        List<UUID> stockIds = stockRepository.cascadeDeleteFromOrder(event.order());
        if(!stockIds.isEmpty()){
            for (UUID stockId : stockIds) {
                this.auditStockSoftDelete(stockId, "Cascade delete from order " + event.order());
            }
        }
    }

    @EventListener
    public void onPurchaseSoftDelete(PurchaseSoftDeleteObserver event) {
        List<UUID> stockIds = stockRepository.cascadeDeleteFromPurchase(event.purchase());
        if(!stockIds.isEmpty()){
            for (UUID stockId : stockIds) {
                this.auditStockSoftDelete(stockId, "Cascade delete from purchase " + event.purchase());
            }
        }
    }

    @Async
    public void auditStockSoftDelete(UUID id, String reason){
        publisher.publishEvent(new SoftDeleteLogObserver(id.toString(), EntityType.STOCK_MOVEMENT.getEntityType(), reason));
    }
}
