package org.wita.erp.domain.repositories.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.payment.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByGatewaySessionId(String gatewaySessionId);

    @Query("SELECT p FROM Payment p " +
            "LEFT JOIN p.receivable r " +
            "LEFT JOIN r.order o " +
            "LEFT JOIN o.customerPaymentType cpt " +
            "LEFT JOIN cpt.customer c " +
            "WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(CAST(r.id AS string)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Payment> findBySearchTerm(String searchTerm, Pageable pageable);
}
