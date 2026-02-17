package org.wita.erp.domain.repositories.stock;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.stock.StockMovement;

import java.util.List;
import java.util.UUID;

public interface StockRepository extends JpaRepository<StockMovement, UUID> {
    @Query("SELECT s FROM StockMovement s WHERE " +
            "(LOWER(s.product.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "LOWER(CAST(s.id AS STRING)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<StockMovement> findBySearchTerm(String searchTerm, Pageable pageable);

    StockMovement findByTransactionIdAndProductId(
            UUID transaction,
            UUID product
    );

    @Modifying
    @Query(value = """
    UPDATE stock_movement
    SET active = false
    WHERE product_id = :productId
    RETURNING id
""", nativeQuery = true)
    List<UUID> cascadeDeleteFromProduct(UUID productId);

    @Modifying
    @Query(value = """
    UPDATE stock_movement
    SET active = false
    WHERE movement_reason_id = :movementReasonId
    RETURNING id
""", nativeQuery = true)
    List<UUID> cascadeDeleteFromMovementReason(UUID movementReasonId);

    @Modifying
    @Query(value = """
    UPDATE stock_movement
    SET active = false
    WHERE transaction_id = :orderId
    RETURNING id
""", nativeQuery = true)
    List<UUID> cascadeDeleteFromOrder(UUID orderId);

    @Modifying
    @Query(value = """
    UPDATE stock_movement
    SET active = false
    WHERE transaction_id = :purchaseId
    RETURNING id
""", nativeQuery = true)
    List<UUID> cascadeDeleteFromPurchase(UUID purchaseId);
}
