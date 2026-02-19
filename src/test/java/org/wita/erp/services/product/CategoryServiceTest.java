package org.wita.erp.services.product;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.wita.erp.domain.entities.product.dtos.CategoryDTO;
import org.wita.erp.domain.entities.product.dtos.CreateCategoryRequestDTO;
import org.wita.erp.domain.entities.product.dtos.DeleteCategoryRequestDTO;
import org.wita.erp.domain.entities.product.dtos.UpdateCategoryRequestDTO;
import org.wita.erp.domain.entities.product.mappers.CategoryMapper;
import org.wita.erp.domain.repositories.product.CategoryRepository;
import org.wita.erp.infra.exceptions.product.CategoryException;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;
import org.wita.erp.services.product.observers.CategorySoftDeleteObserver;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private CategoryService categoryService;

    private UUID categoryId;
    private Category fakeCategory;
    private Pageable pageable;
    private Page<Category> fakePage;
    private CategoryDTO fakeCategoryDTO;
    private CreateCategoryRequestDTO fakeCreateCategoryDTO;
    UpdateCategoryRequestDTO fakeUpdateCategoryDTO;

    @BeforeEach
    void setup() {
        categoryId = UUID.randomUUID();
        pageable = PageRequest.of(0, 10);

        fakeCategory = new Category();
        fakeCategory.setId(categoryId);
        fakeCategory.setName("Category");
        fakeCategory.setActive(true);

        fakePage = new PageImpl<>(List.of(fakeCategory));

        fakeCategoryDTO = new CategoryDTO(categoryId, "Category", true);
        fakeCreateCategoryDTO = new CreateCategoryRequestDTO("Cleaning");
        fakeUpdateCategoryDTO= new UpdateCategoryRequestDTO("Food");
    }

    @Test
    @DisplayName("Deve retornar todos os produtos quando o searchTerm for nulo")
    void shouldReturnAllProductsWhenSearchTermIsNull() {


        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(fakePage);
        Mockito.when(categoryMapper.toDTO(fakeCategory)).thenReturn(fakeCategoryDTO);

        ResponseEntity<Page<CategoryDTO>> response = categoryService.getAllCategories(pageable, null);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().getTotalElements());
        Mockito.verify(categoryRepository).findAll(pageable);
        Mockito.verify(categoryRepository, Mockito.never()).findBySearchTerm(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("Deve retornar produtos filtrados pelo searchTerm")
    void shouldReturnProductsFilteredBySearchTerm() {
        Mockito.when(categoryRepository.findBySearchTerm("Category", pageable)).thenReturn(fakePage);
        Mockito.when(categoryMapper.toDTO(fakeCategory)).thenReturn(fakeCategoryDTO);

        ResponseEntity<Page<CategoryDTO>> response = categoryService.getAllCategories(pageable, "Category");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().getTotalElements());
        Mockito.verify(categoryRepository).findBySearchTerm("Category", pageable);
        Mockito.verify(categoryRepository, Mockito.never()).findAll(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Deve salvar uma nova categoria com sucesso")
    void shouldSaveCategorySuccessfully() {
        Mockito.when(categoryRepository.findByName(fakeCreateCategoryDTO.name())).thenReturn(null);
        Mockito.when(categoryMapper.toDTO(Mockito.any(Category.class))).thenReturn(fakeCategoryDTO);

        ResponseEntity<CategoryDTO> response = categoryService.save(fakeCreateCategoryDTO);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(fakeCategoryDTO, response.getBody());

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Mockito.verify(categoryRepository).save(Mockito.any(Category.class));
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(fakeCategoryDTO, response.getBody());
    }

    @Test
    @DisplayName("Deve lançar CategoryException quando já existe uma categoria com o mesmo nome")
    void shouldThrowCategoryExceptionWhenCategoryNameExists() {
        Category existingCategory = new Category();

        Mockito.when(categoryRepository.findByName(fakeCreateCategoryDTO.name())).thenReturn(existingCategory);

        CategoryException exception = Assertions.assertThrows(CategoryException.class,
                () -> categoryService.save(fakeCreateCategoryDTO));

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        Assertions.assertEquals("Category already exists", exception.getMessage());
        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any(Category.class));
    }

    @Test
    @DisplayName("Deve atualizar a categoria com sucesso")
    void shouldUpdateCategorySuccessfully() {
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(fakeCategory));
        Mockito.when(categoryRepository.findByName(fakeUpdateCategoryDTO.name())).thenReturn(null);
        Mockito.when(categoryMapper.toDTO(fakeCategory)).thenReturn(new CategoryDTO(categoryId, fakeUpdateCategoryDTO.name(), true));

        ResponseEntity<CategoryDTO> response = categoryService.update(categoryId, fakeUpdateCategoryDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(fakeUpdateCategoryDTO.name(), fakeCategory.getName());
        Mockito.verify(categoryRepository).save(fakeCategory);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar categoria inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentCategory() {
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Assertions.assertThrows(CategoryException.class, () -> categoryService.update(categoryId, fakeUpdateCategoryDTO));
        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve lançar CategoryException quando o nome já pertence a outra categoria")
    void shouldThrowExceptionWhenNewNameAlreadyExistsInAnotherCategory() {
        Category anotherCategory = new Category();
        anotherCategory.setId(UUID.randomUUID());
        anotherCategory.setName("Food");

        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(fakeCategory));
        Mockito.when(categoryRepository.findByName("Food")).thenReturn(anotherCategory);

        CategoryException exception = Assertions.assertThrows(CategoryException.class,
                () -> categoryService.update(categoryId, fakeUpdateCategoryDTO));

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        Assertions.assertEquals("Another category with the same name already exists", exception.getMessage());
        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve realizar soft delete da categoria e disparar eventos")
    void shouldDeleteCategorySuccessfully() {
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(fakeCategory));
        Mockito.when(categoryMapper.toDTO(fakeCategory)).thenReturn(fakeCategoryDTO);

        ResponseEntity<CategoryDTO> response = categoryService.delete(categoryId, new DeleteCategoryRequestDTO("Reason"));

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertFalse(fakeCategory.getActive());

        Mockito.verify(categoryRepository).save(fakeCategory);
        Mockito.verify(publisher).publishEvent(Mockito.any(SoftDeleteLogObserver.class));
        Mockito.verify(publisher).publishEvent(Mockito.any(CategorySoftDeleteObserver.class));
    }

    @Test
    @DisplayName("Deve lançar CategoryException ao tentar deletar uma categoria inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        DeleteCategoryRequestDTO reason = new DeleteCategoryRequestDTO("Reason");

        CategoryException exception = Assertions.assertThrows(CategoryException.class,
                () -> categoryService.delete(categoryId, reason));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(publisher, Mockito.never()).publishEvent(Mockito.any());
    }
}