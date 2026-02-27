package org.wita.erp.domain.repositories.paymentType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wita.erp.domain.entities.paymentType.PaymentType;

import java.util.UUID;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, UUID> {

}
