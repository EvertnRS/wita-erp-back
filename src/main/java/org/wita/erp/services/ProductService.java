package org.wita.erp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.product.Category;
import org.wita.erp.domain.product.dtos.CreateProductRequestDTO;
import org.wita.erp.domain.product.dtos.UpdateProductRequestDTO;
import org.wita.erp.domain.product.mappers.ProductMapper;
import org.wita.erp.domain.product.Product;
import org.wita.erp.infra.exceptions.product.CategoryException;
import org.wita.erp.infra.exceptions.product.ProductException;
import org.wita.erp.repositories.CategoryRepository;
import org.wita.erp.repositories.ProductRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

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

        if (productRepository.findByName(data.name()) != null) {
            throw new ProductException("Product already exists", HttpStatus.CONFLICT);
        }

        Product product = new Product();
        product.setName(data.name());
        product.setPrice(data.price());
        product.setMinQuantity(data.minQuantity());
        product.setQuantityInStock(data.quantityInStock());
        product.setCategory(category);

        productRepository.save(product);

        return ResponseEntity.ok(product);
    }

    public ResponseEntity<Product> update(UUID id, UpdateProductRequestDTO data) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found", HttpStatus.NOT_FOUND));

        if (data.category() != null) {
            Category category = categoryRepository.findById(data.category())
                    .orElseThrow(() -> new CategoryException("Category not registered in the system", HttpStatus.NOT_FOUND));
            product.setCategory(category);
        }

        productMapper.updateProductFromProduct(data, product);
        productRepository.save(product);

        return ResponseEntity.ok(product);
    }

    public ResponseEntity<Product> delete(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found", HttpStatus.NOT_FOUND));
        product.setActive(false);
        productRepository.save(product);
        return ResponseEntity.ok(product);
    }
}
