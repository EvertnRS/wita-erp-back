package org.wita.erp.domain.repositories.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.product.Product;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("SELECT p FROM Product p WHERE " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.category.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "LOWER(p.supplier.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Product> findBySearchTerm(String searchTerm, Pageable pageable);

    Product findByName(String name);
}
