package org.wita.erp.services.stock;

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
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.entities.stock.MovementReason;
import org.wita.erp.domain.entities.stock.StockMovement;
import org.wita.erp.domain.entities.stock.StockMovementType;
import org.wita.erp.domain.entities.stock.dtos.CreateStockRequestDTO;
import org.wita.erp.domain.entities.stock.dtos.UpdateStockRequestDTO;
import org.wita.erp.domain.entities.stock.mappers.StockMapper;
import org.wita.erp.domain.entities.transaction.Transaction;
import org.wita.erp.domain.entities.transaction.order.Order;
import org.wita.erp.domain.entities.transaction.purchase.Purchase;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.repositories.product.ProductRepository;
import org.wita.erp.domain.repositories.stock.MovementReasonRepository;
import org.wita.erp.domain.repositories.stock.StockRepository;
import org.wita.erp.domain.repositories.transaction.TransactionRepository;
import org.wita.erp.domain.repositories.transaction.order.OrderRepository;
import org.wita.erp.domain.repositories.transaction.purchase.PurchaseRepository;
import org.wita.erp.domain.repositories.user.UserRepository;
import org.wita.erp.infra.exceptions.order.OrderException;
import org.wita.erp.infra.exceptions.product.ProductException;
import org.wita.erp.infra.exceptions.purchase.PurchaseException;
import org.wita.erp.infra.exceptions.stock.MovementReasonException;
import org.wita.erp.infra.exceptions.stock.StockException;
import org.wita.erp.infra.exceptions.transaction.TransactionException;
import org.wita.erp.infra.exceptions.user.UserException;
import org.wita.erp.services.stock.observers.StockCompensationOrderObserver;
import org.wita.erp.services.stock.observers.StockCompensationPurchaseObserver;
import org.wita.erp.services.stock.observers.StockMovementObserver;
import org.wita.erp.services.stock.observers.UpdateStockMovementObserver;
import org.wita.erp.services.transaction.order.observers.CreateOrderObserver;
import org.wita.erp.services.transaction.order.observers.UpdateOrderObserver;
import org.wita.erp.services.transaction.purchase.observers.CreatePurchaseObserver;
import org.wita.erp.services.transaction.purchase.observers.UpdatePurchaseObserver;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final MovementReasonRepository movementReasonRepository;
    private final StockMapper stockMapper;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;
    private final PurchaseRepository purchaseRepository;
    private final ApplicationEventPublisher publisher;

    public ResponseEntity<Page<StockMovement>> getAllStock(Pageable pageable, String searchTerm) {
        Page<StockMovement> stockPage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            stockPage = stockRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            stockPage = stockRepository.findAll(pageable);
        }

        return ResponseEntity.ok(stockPage);
    }

    @Transactional
    public ResponseEntity<StockMovement> save(CreateStockRequestDTO data) {
        Product product = productRepository.findById(data.product())
                .orElseThrow(() -> new ProductException("Product not registered in the system", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(data.movementReason())
                .orElseThrow(() -> new MovementReasonException("MovementReason not registered in the system", HttpStatus.NOT_FOUND));

        User user = userRepository.findById(data.user())
                .orElseThrow(() -> new UserException("User not registered in the system", HttpStatus.NOT_FOUND));

        Transaction transaction = transactionRepository.findById(data.transaction())
                .orElseThrow(() -> new TransactionException("Transaction not registered in the system", HttpStatus.NOT_FOUND));

        StockMovement stock = new StockMovement();
        if (transaction instanceof Order) {
            Order order = orderRepository.findById(data.transaction())
                    .orElseThrow(() -> new OrderException("Order not registered in the system", HttpStatus.NOT_FOUND));

            stock.setStockMovementType(StockMovementType.OUT);
            stock.setTransaction(order);
        }

        if (transaction instanceof Purchase){
            Purchase purchase = purchaseRepository.findById(data.transaction())
                    .orElseThrow(() -> new PurchaseException("Purchase not registered in the system", HttpStatus.NOT_FOUND));

            stock.setStockMovementType(StockMovementType.OUT);
            stock.setTransaction(purchase);
        }

        publisher.publishEvent(new StockMovementObserver(stock.getStockMovementType(), product.getId(), data.quantity()));

        stock.setProduct(product);
        stock.setQuantity(data.quantity());
        stock.setMovementReason(movementReason);
        stock.setUser(user);

        stockRepository.save(stock);

        return ResponseEntity.ok(stock);
    }

    public ResponseEntity<StockMovement> update(UUID id, UpdateStockRequestDTO data) {
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

                stock.setStockMovementType(StockMovementType.OUT);
                stock.setTransaction(purchase);
            }
        }

        publisher.publishEvent(new UpdateStockMovementObserver(stock.getStockMovementType(), data.product(), data.quantity()));

        if (data.movementReason() != null) {
            MovementReason movementReason = movementReasonRepository.findById(data.movementReason())
                    .orElseThrow(() -> new MovementReasonException("MovementReason not registered in the system", HttpStatus.NOT_FOUND));
            stock.setMovementReason(movementReason);
        }

        if (data.user() != null) {
            User user = userRepository.findById(data.user())
                    .orElseThrow(() -> new UserException("User not registered in the system", HttpStatus.NOT_FOUND));
            stock.setUser(user);
        }

        stockMapper.updateStockFromDTO(data, stock);
        stockRepository.save(stock);

        return ResponseEntity.ok(stock);
    }

    public ResponseEntity<StockMovement> delete(UUID id) {
        StockMovement stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockException("Stock not found", HttpStatus.NOT_FOUND));
        stock.setActive(false);
        stockRepository.save(stock);
        return ResponseEntity.ok(stock);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    @Async
    public void onOrderCreated(CreateOrderObserver event) {
        try{
            Order order = orderRepository.findById(event.order())
                    .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));

            MovementReason movementReason = movementReasonRepository.findById(event.movementReason())
                    .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

            order.getItems().forEach(orderItem -> {
                CreateStockRequestDTO dto = new CreateStockRequestDTO(orderItem.getProduct().getId(), orderItem.getQuantity(), movementReason.getId(), order.getId(), order.getSeller().getId());
                this.save(dto);
            });

        } catch (Exception e) {
            publisher.publishEvent(new StockCompensationOrderObserver(event.order()));
            throw new StockException("Failed to process stock movements for order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    @Async
    public void onPurchaseCreated(CreatePurchaseObserver event) {
        try{
            Purchase purchase = purchaseRepository.findById(event.purchase())
                    .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));

            MovementReason movementReason = movementReasonRepository.findById(event.movementReason())
                    .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

            purchase.getItems().forEach(purchaseItem -> {
                CreateStockRequestDTO dto = new CreateStockRequestDTO(purchaseItem.getProduct().getId(), purchaseItem.getQuantity(), movementReason.getId(), purchase.getId(), purchase.getBuyer().getId());
                this.save(dto);
            });

        } catch (Exception e) {
            publisher.publishEvent(new StockCompensationPurchaseObserver(event.purchase()));
            throw new StockException("Failed to process stock movements for order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    public void onOrderUpdated(UpdateOrderObserver event) {

        Order order = orderRepository.findById(event.order())
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(event.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

        order.getItems().forEach(orderItem -> {
            StockMovement stockMovement = stockRepository.findByTransactionAndProduct(order, orderItem.getProduct());
            UpdateStockRequestDTO dto = new UpdateStockRequestDTO(orderItem.getProduct().getId(), orderItem.getQuantity(), movementReason.getId(), order.getId(), order.getSeller().getId());
            this.update(stockMovement.getId(), dto);
        });
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    public void onPurchaseUpdated(UpdatePurchaseObserver event) {

        Purchase purchase = purchaseRepository.findById(event.purchase())
                .orElseThrow(() -> new PurchaseException("Purchase not found", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(event.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

        purchase.getItems().forEach(purchaseItem -> {
            StockMovement stockMovement = stockRepository.findByTransactionAndProduct(purchase, purchaseItem.getProduct());
            UpdateStockRequestDTO dto = new UpdateStockRequestDTO(purchaseItem.getProduct().getId(), purchaseItem.getQuantity(), movementReason.getId(), purchase.getId(), purchase.getBuyer().getId());
            this.update(stockMovement.getId(), dto);
        });
    }
}
