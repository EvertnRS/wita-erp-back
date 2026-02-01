package org.wita.erp.domain.repositories.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.order.Order;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(o.seller.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "(LOWER(o.customer.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Order> findBySearchTerm(String searchTerm, Pageable pageable);

    Order findByTransactionCode(String transactionCode);
}
