package org.wita.erp.services.product;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.wita.erp.domain.entities.product.Category;
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.entities.product.dtos.CreateProductRequestDTO;
import org.wita.erp.domain.entities.product.dtos.DeleteProductRequestDTO;
import org.wita.erp.domain.entities.product.dtos.ProductDTO;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private SchedulerService schedulerService;
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private ProductService productService;

    private UUID productId, categoryId, supplierId;
    private Product fakeProduct;
    private Category fakeCategory;
    private Supplier fakeSupplier;
    private Pageable pageable;
    private Page<Product> fakePage;
    private ProductDTO fakeProductDTO;
    private CreateProductRequestDTO fakeCreateDTO;
    private UpdateProductRequestDTO fakeUpdateDTO;

    @BeforeEach
    void setup() {
        productId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        supplierId = UUID.randomUUID();
        pageable = PageRequest.of(0, 10);

        fakeCategory = new Category();
        fakeCategory.setId(categoryId);

        fakeSupplier = new Supplier();
        fakeSupplier.setId(supplierId);

        fakeProduct = new Product();
        fakeProduct.setId(productId);
        fakeProduct.setName("Notebook");
        fakeProduct.setPrice(new BigDecimal("100.00"));
        fakeProduct.setDiscount(BigDecimal.ZERO);
        fakeProduct.setQuantityInStock(50);
        fakeProduct.setMinQuantity(10);
        fakeProduct.setCategory(fakeCategory);
        fakeProduct.setSupplier(fakeSupplier);
        fakeProduct.setActive(true);

        fakePage = new PageImpl<>(List.of(fakeProduct));

        fakeProductDTO = new ProductDTO(productId, "Notebook", new BigDecimal("100.00"), BigDecimal.ZERO, 0, 10, 50, null, null, null, true);
        fakeCreateDTO = new CreateProductRequestDTO("Notebook", new BigDecimal("100.00"), BigDecimal.ZERO, 0, 10, 50, categoryId, supplierId);
        fakeUpdateDTO = new UpdateProductRequestDTO("Notebook Updated", null, null, null, categoryId, supplierId);
    }

    @Test
    @DisplayName("Deve retornar todos os produtos quando o searchTerm for nulo")
    void shouldReturnAllProductsWhenSearchTermIsNull() {
        Mockito.when(productRepository.findAll(pageable)).thenReturn(fakePage);
        Mockito.when(productMapper.toDTO(fakeProduct)).thenReturn(fakeProductDTO);

        ResponseEntity<Page<ProductDTO>> response = productService.getAllProducts(pageable, null);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().getTotalElements());
        Mockito.verify(productRepository).findAll(pageable);
        Mockito.verify(productRepository, Mockito.never()).findBySearchTerm(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("Deve retornar produtos filtrados pelo searchTerm")
    void shouldReturnProductsFilteredBySearchTerm() {
        Mockito.when(productRepository.findBySearchTerm("Notebook", pageable)).thenReturn(fakePage);
        Mockito.when(productMapper.toDTO(fakeProduct)).thenReturn(fakeProductDTO);

        ResponseEntity<Page<ProductDTO>> response = productService.getAllProducts(pageable, "Notebook");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().getTotalElements());
        Mockito.verify(productRepository).findBySearchTerm("Notebook", pageable);
        Mockito.verify(productRepository, Mockito.never()).findAll(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Deve cadastrar produto com sucesso")
    void shouldRegisterProductSuccessfully() {
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(fakeCategory));
        Mockito.when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(fakeSupplier));
        Mockito.when(productRepository.findByName(fakeCreateDTO.name())).thenReturn(null);
        Mockito.when(productMapper.toDTO(Mockito.any(Product.class))).thenReturn(fakeProductDTO);

        ResponseEntity<ProductDTO> response = productService.save(fakeCreateDTO);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Mockito.verify(productRepository).save(Mockito.any(Product.class));
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve lançar CategoryException quando a categoria não for encontrada no cadastro")
    void shouldThrowCategoryExceptionWhenCategoryNotFoundOnSave() {
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        CategoryException exception = Assertions.assertThrows(CategoryException.class,
                () -> productService.save(fakeCreateDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Assertions.assertEquals("Category not registered in the system", exception.getMessage());
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any(Product.class));
    }

    @Test
    @DisplayName("Deve lançar SupplierException quando o fornecedor não for encontrado no cadastro")
    void shouldThrowSupplierExceptionWhenSupplierNotFoundOnSave() {
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(fakeCategory));
        Mockito.when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        SupplierException exception = Assertions.assertThrows(SupplierException.class,
                () -> productService.save(fakeCreateDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Assertions.assertEquals("Supplier not registered in the system", exception.getMessage());
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any(Product.class));
    }

    @Test
    @DisplayName("Deve lançar ProductException quando tentar cadastrar um produto com nome já existente")
    void shouldThrowProductExceptionWhenProductNameAlreadyExistsOnSave() {
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(fakeCategory));
        Mockito.when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(fakeSupplier));
        Mockito.when(productRepository.findByName(fakeCreateDTO.name())).thenReturn(new Product());

        ProductException exception = Assertions.assertThrows(ProductException.class,
                () -> productService.save(fakeCreateDTO));

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        Assertions.assertEquals("Product already exists", exception.getMessage());
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any(Product.class));
    }

    @Test
    @DisplayName("Deve cadastrar produto mantendo minQuantityForDiscount quando o desconto for maior que zero")
    void shouldRegisterProductKeepingMinQuantityWhenDiscountIsGreaterThanZero() {
        CreateProductRequestDTO dtoWithDiscount = new CreateProductRequestDTO(
                "Monitor", new BigDecimal("1000.00"), new BigDecimal("0.10"), 5, 10, 50, categoryId, supplierId
        );

        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(fakeCategory));
        Mockito.when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(fakeSupplier));
        Mockito.when(productRepository.findByName(dtoWithDiscount.name())).thenReturn(null);
        Mockito.when(productMapper.toDTO(Mockito.any(Product.class))).thenReturn(fakeProductDTO);

        productService.save(dtoWithDiscount);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productRepository).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();

        Assertions.assertEquals(new BigDecimal("0.10"), savedProduct.getDiscount());
        Assertions.assertEquals(5, savedProduct.getMinQuantityForDiscount());
    }

    @Test
    @DisplayName("Deve cadastrar produto forçando minQuantityForDiscount a zero quando não houver desconto")
    void shouldRegisterProductForcingMinQuantityToZeroWhenNoDiscount() {
        CreateProductRequestDTO dtoWithoutDiscount = new CreateProductRequestDTO(
                "Teclado", new BigDecimal("100.00"), BigDecimal.ZERO, 99, 10, 50, categoryId, supplierId
        );

        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(fakeCategory));
        Mockito.when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(fakeSupplier));
        Mockito.when(productRepository.findByName(dtoWithoutDiscount.name())).thenReturn(null);
        Mockito.when(productMapper.toDTO(Mockito.any(Product.class))).thenReturn(fakeProductDTO);

        productService.save(dtoWithoutDiscount);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productRepository).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();

        Assertions.assertEquals(BigDecimal.ZERO, savedProduct.getDiscount());
        Assertions.assertEquals(0, savedProduct.getMinQuantityForDiscount());
    }

    @Test
    @DisplayName("Deve atualizar produto e agendar reposição se estoque ficar abaixo do mínimo")
    void shouldUpdateProductAndScheduleReplenishment() {
        fakeProduct.setQuantityInStock(5);

        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(fakeProduct));
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(fakeCategory));
        Mockito.when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(fakeSupplier));
        Mockito.when(productMapper.toDTO(fakeProduct)).thenReturn(fakeProductDTO);

        ResponseEntity<ProductDTO> response = productService.update(productId, fakeUpdateDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(productMapper).updateProductFromDTO(fakeUpdateDTO, fakeProduct);
        Mockito.verify(productRepository).save(fakeProduct);
        Mockito.verify(schedulerService).schedule(
                Mockito.eq(ScheduledTaskTypes.PRODUCT_REPLENISHMENT),
                Mockito.eq(productId.toString()),
                Mockito.any(LocalDateTime.class)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar produto inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductException.class, () -> productService.update(productId, fakeUpdateDTO));
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve lançar CategoryException ao tentar atualizar com uma categoria inexistente")
    void shouldThrowCategoryExceptionWhenCategoryNotFoundOnUpdate() {
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(fakeProduct));
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        CategoryException exception = Assertions.assertThrows(CategoryException.class,
                () -> productService.update(productId, fakeUpdateDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Assertions.assertEquals("Category not registered in the system", exception.getMessage());
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any(Product.class));
    }

    @Test
    @DisplayName("Deve lançar SupplierException ao tentar atualizar com um fornecedor inexistente")
    void shouldThrowSupplierExceptionWhenSupplierNotFoundOnUpdate() {
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(fakeProduct));
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(fakeCategory));
        Mockito.when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        SupplierException exception = Assertions.assertThrows(SupplierException.class,
                () -> productService.update(productId, fakeUpdateDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Assertions.assertEquals("Supplier not registered in the system", exception.getMessage());
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any(Product.class));
    }

    @Test
    @DisplayName("Deve realizar soft delete do produto e disparar eventos")
    void shouldDeleteProductSuccessfully() {
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(fakeProduct));
        Mockito.when(productMapper.toDTO(fakeProduct)).thenReturn(fakeProductDTO);

        ResponseEntity<ProductDTO> response = productService.delete(productId, new DeleteProductRequestDTO("Reason"));

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertFalse(fakeProduct.getActive());

        Mockito.verify(productRepository).save(fakeProduct);
        Mockito.verify(publisher).publishEvent(Mockito.any(SoftDeleteLogObserver.class));
        Mockito.verify(publisher).publishEvent(Mockito.any(ProductSoftDeleteObserver.class));
    }

    @Test
    @DisplayName("Deve lançar ProductException ao tentar deletar um produto inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.empty());
        DeleteProductRequestDTO reason = new DeleteProductRequestDTO("Reason");

        ProductException exception = Assertions.assertThrows(ProductException.class,
                () -> productService.delete(productId, reason));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(publisher, Mockito.never()).publishEvent(Mockito.any());
    }

    @Test
    @DisplayName("Deve lançar ProductException se o produto não for encontrado ao processar entrada/saída de estoque")
    void shouldThrowExceptionWhenProductNotFoundOnCreateStockMovement() {
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.empty());
        StockMovementObserver event = new StockMovementObserver(StockMovementType.IN, productId, 10);

        ProductException exception = Assertions.assertThrows(ProductException.class,
                () -> productService.onCreateStockMovement(event));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve lançar ProductException se o produto não for encontrado ao atualizar movimento de estoque")
    void shouldThrowExceptionWhenProductNotFoundOnUpdateStockMovement() {
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.empty());
        UpdateStockMovementObserver event = new UpdateStockMovementObserver(StockMovementType.IN, productId, 10);

        ProductException exception = Assertions.assertThrows(ProductException.class,
                () -> productService.onUpdateStockMovement(event));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    @DisplayName("Deve adicionar estoque ao receber evento de entrada (IN)")
    void shouldAddStockOnMovementIn() {
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(fakeProduct));
        StockMovementObserver event = new StockMovementObserver(StockMovementType.IN, productId, 20);

        productService.onCreateStockMovement(event);

        Assertions.assertEquals(70, fakeProduct.getQuantityInStock());
        Mockito.verify(productRepository).save(fakeProduct);
    }

    @Test
    @DisplayName("Deve subtrair estoque e agendar reposição ao receber evento de saída (OUT)")
    void shouldSubtractStockOnMovementOut() {
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(fakeProduct));
        StockMovementObserver event = new StockMovementObserver(StockMovementType.OUT, productId, 45);

        productService.onCreateStockMovement(event);

        Assertions.assertEquals(5, fakeProduct.getQuantityInStock());
        Mockito.verify(productRepository).save(fakeProduct);
        Mockito.verify(schedulerService).schedule(Mockito.eq(ScheduledTaskTypes.PRODUCT_REPLENISHMENT), Mockito.eq(productId.toString()), Mockito.any());
    }

    @Test
    @DisplayName("Deve lançar exceção se tentar tirar mais estoque do que o disponível")
    void shouldThrowExceptionWhenStockIsInsufficient() {
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(fakeProduct));
        StockMovementObserver event = new StockMovementObserver(StockMovementType.OUT, productId,100);

        Assertions.assertThrows(ProductException.class, () -> productService.onCreateStockMovement(event));
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve ajustar quantidade de estoque ao atualizar um movimento")
    void shouldAdjustStockOnUpdateMovement() {
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(fakeProduct));
        UpdateStockMovementObserver event = new UpdateStockMovementObserver(StockMovementType.IN, productId,30);

        productService.onUpdateStockMovement(event);

        Assertions.assertEquals(30, fakeProduct.getQuantityInStock());
        Mockito.verify(productRepository).save(fakeProduct);
    }

    @Test
    @DisplayName("Deve deletar produtos em cascata quando a categoria for deletada")
    void shouldCascadeDeleteWhenCategoryDeleted() {
        UUID prod2Id = UUID.randomUUID();
        Mockito.when(productRepository.cascadeDeleteFromCategory(categoryId)).thenReturn(List.of(productId, prod2Id));

        productService.onCategorySoftDelete(new CategorySoftDeleteObserver(categoryId));

        Mockito.verify(publisher, Mockito.times(2)).publishEvent(Mockito.any(SoftDeleteLogObserver.class));
        Mockito.verify(publisher, Mockito.times(2)).publishEvent(Mockito.any(ProductSoftDeleteObserver.class));
    }

    @Test
    @DisplayName("Deve deletar produtos em cascata quando o fornecedor for deletado")
    void shouldCascadeDeleteWhenSupplierDeleted() {
        Mockito.when(productRepository.cascadeDeleteFromSupplier(supplierId)).thenReturn(List.of(productId));

        productService.onSupplierSoftDelete(new SupplierSoftDeleteObserver(supplierId));

        Mockito.verify(publisher, Mockito.times(1)).publishEvent(Mockito.any(SoftDeleteLogObserver.class));
        Mockito.verify(publisher, Mockito.times(1)).publishEvent(Mockito.any(ProductSoftDeleteObserver.class));
    }
}