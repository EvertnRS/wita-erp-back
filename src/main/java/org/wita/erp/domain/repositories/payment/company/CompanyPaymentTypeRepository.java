package org.wita.erp.domain.repositories.payment.company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.company.CompanyPaymentType;
import org.wita.erp.domain.entities.payment.customer.CustomerPaymentType;

import java.util.Optional;
import java.util.UUID;

public interface CompanyPaymentTypeRepository extends JpaRepository<CompanyPaymentType , UUID> {
    @Query("""
    SELECT c FROM CompanyPaymentType c WHERE
     LOWER(FUNCTION('TO_CHAR', c.paymentMethod)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
""")
    Page<CompanyPaymentType> findBySearchTerm(
            String searchTerm,
            Pageable pageable
    );
}
