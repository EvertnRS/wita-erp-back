package org.wita.erp.domain.repositories.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.product.Category;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    @Query("SELECT c FROM Category c WHERE " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "LOWER(CAST(c.id AS STRING)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Category> findBySearchTerm(String searchTerm, Pageable pageable);

    Category findByName(String name);
}
