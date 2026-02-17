package org.wita.erp.controllers.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.controllers.product.docs.ProductDocs;
import org.wita.erp.domain.entities.product.dtos.CreateProductRequestDTO;
import org.wita.erp.domain.entities.product.dtos.ProductDTO;
import org.wita.erp.domain.entities.product.dtos.DeleteProductRequestDTO;
import org.wita.erp.domain.entities.product.dtos.UpdateProductRequestDTO;
import org.wita.erp.services.product.ProductService;

import java.util.UUID;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController implements ProductDocs {
    private final ProductService productService;

    @GetMapping(produces = "application/json")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(@PageableDefault(size = 10, sort = "name") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return productService.getAllProducts(pageable, searchTerm);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody CreateProductRequestDTO data) {
        return productService.save(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    public ResponseEntity<ProductDTO> update(@PathVariable UUID id, @RequestBody @Valid UpdateProductRequestDTO data) {
        return productService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    public ResponseEntity<ProductDTO> delete(@PathVariable UUID id, @RequestBody @Valid DeleteProductRequestDTO data) {
        return productService.delete(id, data);
    }
}
