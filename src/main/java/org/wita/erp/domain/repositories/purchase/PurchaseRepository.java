package org.wita.erp.domain.repositories.purchase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.purchase.Purchase;

import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
    @Query("SELECT p FROM Purchase p WHERE " +
            "(LOWER(p.supplier.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Purchase> findBySearchTerm(String searchTerm, Pageable pageable);
}
