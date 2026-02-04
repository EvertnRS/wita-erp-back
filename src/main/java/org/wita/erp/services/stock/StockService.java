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
import org.wita.erp.domain.entities.order.Order;
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.entities.stock.MovementReason;
import org.wita.erp.domain.entities.stock.StockMovement;
import org.wita.erp.domain.entities.stock.StockMovementType;
import org.wita.erp.domain.entities.stock.dtos.CreateStockRequestDTO;
import org.wita.erp.domain.entities.stock.dtos.UpdateStockRequestDTO;
import org.wita.erp.domain.entities.stock.mappers.StockMapper;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.repositories.order.OrderRepository;
import org.wita.erp.domain.repositories.product.ProductRepository;
import org.wita.erp.domain.repositories.stock.MovementReasonRepository;
import org.wita.erp.domain.repositories.user.UserRepository;
import org.wita.erp.infra.exceptions.order.OrderException;
import org.wita.erp.infra.exceptions.product.ProductException;
import org.wita.erp.infra.exceptions.stock.MovementReasonException;
import org.wita.erp.infra.exceptions.stock.StockException;
import org.wita.erp.domain.repositories.stock.StockRepository;
import org.wita.erp.infra.exceptions.user.UserException;
import org.wita.erp.services.order.CreateOrderObserver;
import org.wita.erp.services.product.ProductService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final MovementReasonRepository movementReasonRepository;
    private final StockMapper stockMapper;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductService productService;
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

        if(data.stockMovementType() == StockMovementType.IN) {
            productService.addProductInStock(data.product(), data.quantity());
        } else if (data.stockMovementType() == StockMovementType.OUT) {
            productService.removeProductFromStock(data.product(), data.quantity());
        }

        StockMovement stock = new StockMovement();
        stock.setProduct(product);
        stock.setStockMovementType(data.stockMovementType());
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

        if (data.movementReason() != null) {
            MovementReason movementReason = movementReasonRepository.findById(data.movementReason())
                    .orElseThrow(() -> new MovementReasonException("MovementReason not registered in the system", HttpStatus.NOT_FOUND));
            stock.setMovementReason(movementReason);
        }

        if (data.product() != null) {
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
                CreateStockRequestDTO dto = new CreateStockRequestDTO(orderItem.getProduct().getId(), StockMovementType.OUT, orderItem.getQuantity(), movementReason.getId(), order.getSeller().getId());
                this.save(dto);
            });
        } catch (Exception e) {
            publisher.publishEvent(new StockCompensationObserver(event.order()));
            throw new StockException("Failed to process stock movements for order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
