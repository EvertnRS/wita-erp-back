package org.wita.erp.domain.repositories.payment.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.payment.customer.CustomerPaymentType;

import java.util.UUID;

public interface CustomerPaymentTypeRepository extends JpaRepository<CustomerPaymentType, UUID> {
    @Query("""
    SELECT c FROM CustomerPaymentType c WHERE
    LOWER(FUNCTION('TO_CHAR', c.customer.name)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
""")
    Page<CustomerPaymentType> findBySearchTerm(
            String searchTerm,
            Pageable pageable
    );


}
