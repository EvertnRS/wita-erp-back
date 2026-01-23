package org.wita.erp.controllers.Product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.Product.Dtos.CreateProductRequestDTO;
import org.wita.erp.domain.Product.Dtos.UpdateProductRequestDTO;
import org.wita.erp.domain.Product.Product;
import org.wita.erp.services.ProductService;

import java.util.UUID;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    public ResponseEntity<Page<Product>> getAllUsers(@PageableDefault(size = 10, sort = "name") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return productService.getAllProducts(pageable, searchTerm);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    public ResponseEntity<Product> create(@Valid @RequestBody CreateProductRequestDTO data) {
        return productService.save(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    public ResponseEntity<Product> update(@PathVariable UUID id, @RequestBody @Valid UpdateProductRequestDTO data) {
        return productService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    public ResponseEntity<Product> delete(@PathVariable UUID id) {
        return productService.delete(id);
    }
}
