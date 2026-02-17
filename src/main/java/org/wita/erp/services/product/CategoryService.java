package org.wita.erp.services.product;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wita.erp.domain.entities.audit.EntityType;
import org.wita.erp.domain.entities.product.Category;
import org.wita.erp.domain.entities.product.dtos.CategoryDTO;
import org.wita.erp.domain.entities.product.dtos.CreateCategoryRequestDTO;
import org.wita.erp.domain.entities.product.dtos.DeleteCategoryRequestDTO;
import org.wita.erp.domain.entities.product.dtos.UpdateCategoryRequestDTO;
import org.wita.erp.domain.entities.product.mappers.CategoryMapper;
import org.wita.erp.infra.exceptions.product.CategoryException;
import org.wita.erp.domain.repositories.product.CategoryRepository;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;
import org.wita.erp.services.product.observers.CategorySoftDeleteObserver;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public ResponseEntity<Page<CategoryDTO>> getAllCategories(Pageable pageable, String searchTerm) {
        Page<Category> categoryPage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            categoryPage = categoryRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            categoryPage = categoryRepository.findAll(pageable);
        }

        return ResponseEntity.ok(categoryPage.map(categoryMapper::toDTO));
    }

    public ResponseEntity<CategoryDTO> save(CreateCategoryRequestDTO data) {
        if (categoryRepository.findByName(data.name()) != null) {
            throw new CategoryException("Category already exists", HttpStatus.CONFLICT);
        }

        Category category = new Category();
        category.setName(data.name());

        categoryRepository.save(category);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toDTO(category));
    }

    public ResponseEntity<CategoryDTO> update(UUID id, UpdateCategoryRequestDTO data) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException("Category not found", HttpStatus.NOT_FOUND));

        if(data.name() != null) {
            category.setName(data.name());
        }

        categoryRepository.save(category);

        return ResponseEntity.ok(categoryMapper.toDTO(category));
    }

    public ResponseEntity<CategoryDTO> delete(UUID id, DeleteCategoryRequestDTO data) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException("Category not found", HttpStatus.NOT_FOUND));
        category.setActive(false);

        categoryRepository.save(category);

        this.auditCategorySoftDelete(id, data.reason());
        this.categoryCascadeDelete(id);

        return ResponseEntity.ok(categoryMapper.toDTO(category));
    }

    @Async
    public void auditCategorySoftDelete(UUID id, String reason){
        publisher.publishEvent(new SoftDeleteLogObserver(id.toString(), EntityType.CATEGORY.getEntityType(), reason));
    }

    @Async
    public void categoryCascadeDelete(UUID id){
        publisher.publishEvent(new CategorySoftDeleteObserver(id));
    }
}
