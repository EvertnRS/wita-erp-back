package org.wita.erp.domain.repositories.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.payment.CompanyPaymentType;
import org.wita.erp.domain.entities.payment.CustomerPaymentType;
import org.wita.erp.domain.entities.payment.PaymentType;

import java.util.Optional;
import java.util.UUID;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, UUID> {
    @Query("""
    SELECT p FROM PaymentType p
    WHERE TYPE(p) = CustomerPaymentType
      AND LOWER(FUNCTION('TO_CHAR', p.paymentMethod)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
""")
    Page<PaymentType> findCustomerPaymentTypesBySearchTerm(
            String searchTerm,
            Pageable pageable
    );

    @Query("""
    SELECT p FROM PaymentType p
    WHERE TYPE(p) = CustomerPaymentType
""")
    Page<PaymentType> findAllCustomerPaymentTypes(
            Pageable pageable
    );

    @Query("""
    SELECT c FROM CustomerPaymentType c
    WHERE c.id = :id
""")
    Optional<CustomerPaymentType> findCustomerPaymentTypeById(UUID id);

    @Query("""
    SELECT p FROM PaymentType p
    WHERE TYPE(p) = CompanyPaymentType
      AND LOWER(FUNCTION('TO_CHAR', p.paymentMethod)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
""")
    Page<PaymentType> findCompanyPaymentTypesBySearchTerm(
            String searchTerm,
            Pageable pageable
    );

    @Query("""
    SELECT p FROM PaymentType p
    WHERE TYPE(p) = CompanyPaymentType
""")
    Page<PaymentType> findAllCompanyPaymentTypes(
            Pageable pageable
    );

    @Query("""
    SELECT c FROM CompanyPaymentType c
    WHERE c.id = :id
""")
    Optional<CompanyPaymentType> findCompanyPaymentTypeById(UUID id);
}
