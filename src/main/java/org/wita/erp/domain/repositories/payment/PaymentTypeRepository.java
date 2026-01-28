package org.wita.erp.domain.repositories.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.payment.PaymentType;

import java.util.UUID;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, UUID> {
    @Query("SELECT p FROM PaymentType p WHERE " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<PaymentType> findBySearchTerm(String searchTerm, Pageable pageable);
}
