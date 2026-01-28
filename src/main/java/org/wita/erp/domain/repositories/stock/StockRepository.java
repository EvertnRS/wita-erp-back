package org.wita.erp.domain.repositories.stock;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.stock.StockMovement;

import java.util.UUID;

public interface StockRepository extends JpaRepository<StockMovement, UUID> {
    @Query("SELECT s FROM StockMovement s WHERE " +
            "(LOWER(s.product.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<StockMovement> findBySearchTerm(String searchTerm, Pageable pageable);
}
