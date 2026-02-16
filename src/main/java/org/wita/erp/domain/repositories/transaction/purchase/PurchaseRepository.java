package org.wita.erp.domain.repositories.transaction.purchase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.transaction.purchase.Purchase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
    @Query("SELECT p FROM Purchase p WHERE " +
            "(LOWER(p.buyer.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "(LOWER(p.supplier.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "LOWER(CAST(p.id AS STRING)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Purchase> findBySearchTerm(String searchTerm, Pageable pageable);

    Purchase findByTransactionCode(String transactionCode);

    @Modifying
    @Query(value = """
    UPDATE transaction t
    SET active = false
    FROM purchase p
    WHERE t.id = p.id
    AND p.buyer_id = :userId
    RETURNING t.id;
""", nativeQuery = true)
    List<UUID> cascadeDeleteFromUser(UUID userId);

    @Modifying
    @Query(value = """
    UPDATE transaction t
    SET active = false
    FROM purchase p
    WHERE t.id = p.id
    AND p.supplier_id = :supplierId
    RETURNING t.id;
""", nativeQuery = true)
    List<UUID> cascadeDeleteFromSupplier(UUID supplierId);

    @Modifying
    @Query(value = """
    UPDATE transaction t
    SET active = false
    FROM purchase p
    WHERE t.id = p.id
    AND p.company_payment_type_id = :companyPaymentTypeId
    RETURNING t.id;
""", nativeQuery = true)
    List<UUID> cascadeDeleteFromCompanyPaymentType(UUID companyPaymentTypeId);

    @Query("""
    SELECT p FROM Purchase p
    LEFT JOIN FETCH p.items i
    LEFT JOIN FETCH i.product
    WHERE p.id = :id
""")
    Optional<Purchase> findByIdWithItems(UUID id);

}
