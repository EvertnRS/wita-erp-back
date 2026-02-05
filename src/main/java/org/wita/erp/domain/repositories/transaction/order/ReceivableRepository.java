package org.wita.erp.domain.repositories.transaction.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.transaction.order.Receivable;
import org.wita.erp.domain.entities.transaction.purchase.Payable;

import java.util.UUID;

public interface ReceivableRepository extends JpaRepository<Receivable, UUID> {
    @Query("SELECT r FROM Receivable r WHERE " +
            "(LOWER(r.order.seller.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "(LOWER(r.order.customer.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Receivable> findBySearchTerm(String searchTerm, Pageable pageable);
}
