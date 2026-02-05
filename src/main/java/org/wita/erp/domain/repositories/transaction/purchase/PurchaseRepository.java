package org.wita.erp.domain.repositories.transaction.purchase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.transaction.purchase.Purchase;

import java.util.Optional;
import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
    @Query("SELECT p FROM Purchase p WHERE " +
            "(LOWER(p.buyer.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "(LOWER(p.supplier.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Purchase> findBySearchTerm(String searchTerm, Pageable pageable);

    Purchase findByTransactionCode(String transactionCode);
}
