package org.wita.erp.domain.repositories.transaction.purchase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.transaction.PaymentStatus;
import org.wita.erp.domain.entities.transaction.purchase.Payable;

import java.util.List;
import java.util.UUID;

public interface PayableRepository extends JpaRepository<Payable, UUID> {
    @Query("SELECT p FROM Payable p WHERE " +
            "(LOWER(p.purchase.supplier.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "LOWER(CAST(p.id AS STRING)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Payable> findBySearchTerm(String searchTerm, Pageable pageable);

    @Query("""
    SELECT p FROM Payable p
    WHERE p.purchase.id = :purchaseId
    ORDER BY p.dueDate ASC
""")
    List<Payable> findByPurchaseId(UUID purchaseId);


    @Modifying
    @Query(value = """
    UPDATE payable
    SET active = false
    WHERE purchase_id = :purchaseId
    RETURNING id
""", nativeQuery = true)
    List<UUID> cascadeDeleteFromPurchase(UUID purchaseId);

    @Query("SELECT DISTINCT p.paymentStatus FROM Payable p WHERE p.purchase.id = :purchaseId")
    List<PaymentStatus> findDistinctStatusesByPurchaseId(UUID purchaseId);

}
