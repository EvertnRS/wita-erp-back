package org.wita.erp.domain.repositories.transaction.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.transaction.order.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(o.seller.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "(LOWER(o.customerPaymentType.customer.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "LOWER(CAST(o.id AS STRING)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Order> findBySearchTerm(String searchTerm, Pageable pageable);

    Order findByTransactionCode(String transactionCode);

    @Modifying
    @Query(value = """
    UPDATE transaction t
    SET active = false
    FROM orders o
    WHERE t.id = o.id
    AND o.seller_id = :userId
    RETURNING t.id;
""", nativeQuery = true)
    List<UUID> cascadeDeleteFromUser(UUID userId);

    @Modifying
    @Query(value = """
    UPDATE transaction t
    SET active = false
    FROM orders o
    WHERE t.id = o.id
    AND o.customer_payment_type_id = :customerPaymentTypeId
    RETURNING t.id;
""", nativeQuery = true)
    List<UUID> cascadeDeleteFromCustomerPaymentType(UUID customerPaymentTypeId);

    @Query("""
    SELECT o FROM Order o
    LEFT JOIN FETCH o.items i
    LEFT JOIN FETCH i.product
    WHERE o.id = :id
""")
    Optional<Order> findByIdWithItems(UUID id);

}
