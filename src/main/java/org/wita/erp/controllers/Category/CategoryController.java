package org.wita.erp.controllers.Category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.Product.Category;
import org.wita.erp.domain.Product.Dtos.CreateCategoryRequestDTO;
import org.wita.erp.domain.Product.Dtos.CreateProductRequestDTO;
import org.wita.erp.domain.Product.Dtos.UpdateCategoryRequestDTO;
import org.wita.erp.domain.Product.Dtos.UpdateProductRequestDTO;
import org.wita.erp.domain.Product.Product;
import org.wita.erp.services.CategoryService;
import org.wita.erp.services.ProductService;

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
