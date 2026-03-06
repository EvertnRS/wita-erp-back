package org.wita.erp.domain.repositories.product;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.product.Category;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    @Query("SELECT c FROM Category c WHERE " +
            "(:searchTerm IS NULL OR :searchTerm = '' OR " +
            "  (LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "   LOWER(CAST(c.id AS STRING)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            ") " +
            "AND (:active IS NULL OR c.active = :active)")
    Page<Category> findCategories(Pageable pageable, @Param("searchTerm") String searchTerm, @Param("active") Boolean active);

    Category findByName(String name);
}
