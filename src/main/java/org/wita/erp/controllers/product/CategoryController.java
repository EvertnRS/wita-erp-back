package org.wita.erp.controllers.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.entities.product.Category;
import org.wita.erp.domain.entities.product.dtos.CreateCategoryRequestDTO;
import org.wita.erp.domain.entities.product.dtos.UpdateCategoryRequestDTO;
import org.wita.erp.services.product.CategoryService;

import java.util.UUID;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasAuthority('CATEGORY_READ')")
    public ResponseEntity<Page<Category>> getAllUsers(@PageableDefault(size = 10, sort = "name") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return categoryService.getAllCategories(pageable, searchTerm);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CATEGORY_CREATE')")
    public ResponseEntity<Category> create(@Valid @RequestBody CreateCategoryRequestDTO createCategoryRequestDTO) {
        return categoryService.save(createCategoryRequestDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_UPDATE')")
    public ResponseEntity<Category> update(@PathVariable UUID id, @RequestBody @Valid UpdateCategoryRequestDTO updateCategoryRequestDTO) {
        return categoryService.update(id, updateCategoryRequestDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_DELETE')")
    public ResponseEntity<Category> delete(@PathVariable UUID id) {
        return categoryService.delete(id);
    }
}
