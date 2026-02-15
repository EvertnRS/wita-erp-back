package org.wita.erp.services.product;

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
import org.wita.erp.domain.entities.audit.EntityType;
import org.wita.erp.domain.entities.product.Category;
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.entities.product.dtos.CreateProductRequestDTO;
import org.wita.erp.domain.entities.product.dtos.DeleteProductRequestDTO;
import org.wita.erp.domain.entities.product.dtos.UpdateProductRequestDTO;
import org.wita.erp.domain.entities.product.mappers.ProductMapper;
import org.wita.erp.domain.entities.stock.StockMovementType;
import org.wita.erp.domain.entities.supplier.Supplier;
import org.wita.erp.domain.repositories.product.CategoryRepository;
import org.wita.erp.domain.repositories.product.ProductRepository;
import org.wita.erp.domain.repositories.supplier.SupplierRepository;
import org.wita.erp.infra.exceptions.product.CategoryException;
import org.wita.erp.infra.exceptions.product.ProductException;
import org.wita.erp.infra.exceptions.supplier.SupplierException;
import org.wita.erp.infra.schedules.handler.ScheduledTaskTypes;
import org.wita.erp.infra.schedules.scheduler.SchedulerService;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;
import org.wita.erp.services.product.observers.CategorySoftDeleteObserver;
import org.wita.erp.services.product.observers.ProductSoftDeleteObserver;
import org.wita.erp.services.stock.observers.StockMovementObserver;
import org.wita.erp.services.stock.observers.UpdateStockMovementObserver;
import org.wita.erp.services.supplier.observers.SupplierSoftDeleteObserver;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final SupplierRepository supplierRepository;
    private final SchedulerService schedulerService;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public ResponseEntity<Page<Product>> getAllProducts(Pageable pageable, String searchTerm) {
        Page<Product> productPage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            productPage = productRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        return ResponseEntity.ok(productPage);
    }

    public ResponseEntity<Product> save(CreateProductRequestDTO data) {
        Category category = categoryRepository.findById(data.category())
                .orElseThrow(() -> new CategoryException("Category not registered in the system", HttpStatus.NOT_FOUND));

        Supplier supplier = supplierRepository.findById(data.supplier())
                .orElseThrow(() -> new SupplierException("Supplier not registered in the system", HttpStatus.NOT_FOUND));

        if (productRepository.findByName(data.name()) != null) {
            throw new ProductException("Product already exists", HttpStatus.CONFLICT);
        }

        Product product = getProduct(data, category, supplier);

        productRepository.save(product);

        return ResponseEntity.ok(product);
    }

    private Product getProduct(CreateProductRequestDTO data, Category category, Supplier supplier) {
        Product product = new Product();
        product.setName(data.name());
        product.setPrice(data.price());
        product.setDiscount(data.discount());
        product.setSupplier(supplier);

        if (data.discount().compareTo(BigDecimal.ZERO) > 0) {
            product.setMinQuantityForDiscount(data.minQuantityForDiscount());
        } else {
            product.setMinQuantityForDiscount(0);
        }

        product.setMinQuantity(data.minQuantity());
        product.setQuantityInStock(data.quantityInStock());
        product.setCategory(category);
        return product;
    }

    public ResponseEntity<Product> update(UUID id, UpdateProductRequestDTO data) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found", HttpStatus.NOT_FOUND));

        if (data.category() != null) {
            Category category = categoryRepository.findById(data.category())
                    .orElseThrow(() -> new CategoryException("Category not registered in the system", HttpStatus.NOT_FOUND));
            product.setCategory(category);
        }

        if (data.supplier() != null) {
            Supplier supplier = supplierRepository.findById(data.supplier())
                    .orElseThrow(() -> new SupplierException("Supplier not registered in the system", HttpStatus.NOT_FOUND));
            product.setSupplier(supplier);
        }

        productMapper.updateProductFromDTO(data, product);
        productRepository.save(product);

        if (product.getQuantityInStock() <= product.getMinQuantity()) {
            schedulerService.schedule(
                    ScheduledTaskTypes.PRODUCT_REPLENISHMENT,
                    product.getId().toString(),
                    LocalDate.now().atStartOfDay()
            );
        }

        return ResponseEntity.ok(product);
    }

    public ResponseEntity<Product> delete(UUID id, DeleteProductRequestDTO data) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found", HttpStatus.NOT_FOUND));
        product.setActive(false);

        productRepository.save(product);

        this.auditProductSoftDelete(id, data.reason());
        this.productCascadeDelete(id);

        return ResponseEntity.ok(product);
    }

    @Transactional
    @EventListener
    public void onCreateStockMovement(StockMovementObserver event) {
        Product product = productRepository.findById(event.product())
                .orElseThrow(() -> new ProductException("Product not found", HttpStatus.NOT_FOUND));

        if (event.stockMovementType() == StockMovementType.IN) {
            product.setQuantityInStock(product.getQuantityInStock() + event.quantity());
            productRepository.save(product);
        }

        else if (event.stockMovementType() == StockMovementType.OUT) {
            if(product.getQuantityInStock() < event.quantity()) {
                throw new ProductException("Product quantity out of stock", HttpStatus.CONFLICT);
            }
            product.setQuantityInStock(product.getQuantityInStock() - event.quantity());
            productRepository.save(product);
        }

        if (product.getQuantityInStock() <= product.getMinQuantity()){
            schedulerService.schedule(
                ScheduledTaskTypes.PRODUCT_REPLENISHMENT,
                product.getId().toString(),
                    LocalDate.now().atStartOfDay()
                );
        }
    }

    @Transactional
    @EventListener
    public void onUpdateStockMovement(UpdateStockMovementObserver event) {
        Product product = productRepository.findById(event.product())
                .orElseThrow(() -> new ProductException("Product not found", HttpStatus.NOT_FOUND));

        int adjustedQuantity = event.newQuantity() - product.getQuantityInStock();

        if (adjustedQuantity > 0) {
            product.setQuantityInStock(product.getQuantityInStock() + adjustedQuantity);
        } else if (adjustedQuantity < 0) {
            product.setQuantityInStock(product.getQuantityInStock() + adjustedQuantity);
        }

        productRepository.save(product);

        if (product.getQuantityInStock() <= product.getMinQuantity()) {
            schedulerService.schedule(
                    ScheduledTaskTypes.PRODUCT_REPLENISHMENT,
                    product.getId().toString(),
                    LocalDate.now().atStartOfDay()
            );
        }
    }

    @EventListener
    public void onSupplierSoftDelete(SupplierSoftDeleteObserver event) {
        List<UUID> productIds = productRepository.cascadeDeleteFromSupplier(event.supplier());
        if(!productIds.isEmpty()){
            for (UUID productId : productIds) {
                this.auditProductSoftDelete(productId, "Cascade delete from supplier " + event.supplier());
                this.productCascadeDelete(productId);
            }
        }
    }

    @EventListener
    public void onCategorySoftDelete(CategorySoftDeleteObserver event) {
        List<UUID> productIds = productRepository.cascadeDeleteFromCategory(event.category());
        if(!productIds.isEmpty()){
            for (UUID productId : productIds) {
                this.auditProductSoftDelete(productId, "Cascade delete from category " + event.category());
                this.productCascadeDelete(productId);
            }
        }
    }

    @Async
    public void auditProductSoftDelete(UUID id, String reason){
        publisher.publishEvent(new SoftDeleteLogObserver(id.toString(), EntityType.PRODUCT.getEntityType(), reason));
    }

    @Async
    public void productCascadeDelete(UUID id){
        publisher.publishEvent(new ProductSoftDeleteObserver(id));
    }
}
