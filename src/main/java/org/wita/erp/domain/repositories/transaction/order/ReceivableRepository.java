package org.wita.erp.domain.repositories.transaction.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.transaction.order.Receivable;

import java.util.List;
import java.util.UUID;

public interface ReceivableRepository extends JpaRepository<Receivable, UUID> {
    @Query("SELECT r FROM Receivable r WHERE " +
            "(LOWER(r.order.seller.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "(LOWER(r.order.customerPaymentType.customer.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "LOWER(CAST(r.id AS STRING)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Receivable> findBySearchTerm(String searchTerm, Pageable pageable);

    @Query("""
    SELECT r FROM Receivable r
    WHERE r.order.id = :orderId
    ORDER BY r.dueDate ASC
""")
    List<Receivable> findByOrderId(UUID orderId);

    @Modifying
    @Query(value = """
    UPDATE receivable
    SET active = false
    WHERE order_id = :orderId
    RETURNING id
""", nativeQuery = true)
    List<UUID> cascadeDeleteFromOrder(UUID orderId);
}

