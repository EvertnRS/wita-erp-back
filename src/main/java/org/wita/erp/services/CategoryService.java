package org.wita.erp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.product.Category;
import org.wita.erp.domain.product.dtos.CreateCategoryRequestDTO;
import org.wita.erp.domain.product.dtos.UpdateCategoryRequestDTO;
import org.wita.erp.infra.exceptions.product.CategoryException;
import org.wita.erp.repositories.CategoryRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public ResponseEntity<Page<Category>> getAllCategories(Pageable pageable, String searchTerm) {
        Page<Category> categoryPage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            categoryPage = categoryRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            categoryPage = categoryRepository.findAll(pageable);
        }

        return ResponseEntity.ok(categoryPage);
    }

    public ResponseEntity<Category> save(CreateCategoryRequestDTO data) {
        if (categoryRepository.findByName(data.name()) != null) {
            throw new CategoryException("Category already exists", HttpStatus.CONFLICT);
        }

        Category category = new Category();
        category.setName(data.name());

        categoryRepository.save(category);

        return ResponseEntity.ok(category);
    }

    public ResponseEntity<Category> update(UUID id, UpdateCategoryRequestDTO data) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException("Category not found", HttpStatus.NOT_FOUND));

        if(data.name() != null) {
            category.setName(data.name());
        }

        categoryRepository.save(category);

        return ResponseEntity.ok(category);
    }

    // FIXME: O que acontece com os produtos que tem a categoria que está sendo desativada?
    public ResponseEntity<Category> delete(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException("Category not found", HttpStatus.NOT_FOUND));
        category.setActive(false);
        categoryRepository.save(category);
        return ResponseEntity.ok(category);
    }
}
