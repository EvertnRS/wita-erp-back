package org.wita.erp.domain.repositories.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.transaction.Transaction;
import org.wita.erp.domain.entities.transaction.order.Order;
import org.wita.erp.domain.entities.transaction.purchase.Purchase;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("""
    SELECT t FROM Transaction t
    WHERE TYPE(t) = Purchase
""")
    Page<Purchase> findAllPurchase(
            Pageable pageable
    );

    @Query("""
    SELECT t FROM Transaction t
    WHERE TYPE(t) = Order
""")
    Page<Order> findAllOrder(
            Pageable pageable
    );

    @Query("""
    SELECT r, p
    FROM Receivable r
    LEFT JOIN r.order o
    LEFT JOIN Transaction t1 ON t1.id = o.id,
         Payable p
    LEFT JOIN p.purchase pu
    LEFT JOIN Transaction t2 ON t2.id = pu.id
""")
    Page<?> findAllAccounts(
            Pageable pageable
    );
}
