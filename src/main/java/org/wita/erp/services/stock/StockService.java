package org.wita.erp.services.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.product.Category;
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.entities.stock.MovementReason;
import org.wita.erp.domain.entities.stock.StockMovement;
import org.wita.erp.domain.entities.stock.dtos.CreateStockRequestDTO;
import org.wita.erp.domain.entities.stock.dtos.UpdateStockRequestDTO;
import org.wita.erp.domain.entities.stock.mappers.StockMapper;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.repositories.product.ProductRepository;
import org.wita.erp.domain.repositories.stock.MovementReasonRepository;
import org.wita.erp.domain.repositories.user.UserRepository;
import org.wita.erp.infra.exceptions.product.CategoryException;
import org.wita.erp.infra.exceptions.product.ProductException;
import org.wita.erp.infra.exceptions.stock.MovementReasonException;
import org.wita.erp.infra.exceptions.stock.StockException;
import org.wita.erp.domain.repositories.stock.StockRepository;
import org.wita.erp.infra.exceptions.user.UserException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final MovementReasonRepository movementReasonRepository;
    private final StockMapper stockMapper;
    private final UserRepository userRepository;

    public ResponseEntity<Page<StockMovement>> getAllStock(Pageable pageable) {
        Page<StockMovement> stockPage = stockRepository.findAll(pageable);

        return ResponseEntity.ok(stockPage);
    }

    public ResponseEntity<StockMovement> save(CreateStockRequestDTO data) {
        Product product = productRepository.findById(data.product())
                .orElseThrow(() -> new ProductException("Product not registered in the system", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(data.movementReason())
                .orElseThrow(() -> new MovementReasonException("MovementReason not registered in the system", HttpStatus.NOT_FOUND));

        User user = userRepository.findById(data.user())
                .orElseThrow(() -> new UserException("User not registered in the system", HttpStatus.NOT_FOUND));

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

        stockMapper.updateStockFromStock(data, stock);
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
}
