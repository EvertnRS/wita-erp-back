package org.wita.erp.domain.repositories.payment.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wita.erp.domain.entities.payment.company.CompanyPaymentType;

import java.util.UUID;

public interface CompanyPaymentTypeRepository extends JpaRepository<CompanyPaymentType , UUID> {

}
