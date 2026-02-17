package org.wita.erp.domain.repositories.payment.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.payment.customer.CustomerPaymentType;

import java.util.List;
import java.util.UUID;

public interface CustomerPaymentTypeRepository extends JpaRepository<CustomerPaymentType, UUID> {
    @Query("SELECT c FROM CustomerPaymentType c WHERE " +
    "LOWER(c.customer.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
    "LOWER(CAST(c.id AS STRING)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<CustomerPaymentType> findBySearchTerm(
            String searchTerm,
            Pageable pageable
    );

    @Modifying
    @Query(value = """
    UPDATE payment_type pt
    SET active = false
    FROM customer_payment_type cpt
    WHERE pt.id = cpt.id
    AND cpt.customer_id = :customerId
    RETURNING pt.id;
    
""", nativeQuery = true)
    List<UUID> cascadeDeleteFromCustomer(UUID customerId);

}
