package org.wita.erp.domain.repositories.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wita.erp.domain.entities.payment.PaymentGatewayCustomer;

import java.util.UUID;

public interface PaymentGatewayCustomerRepository extends JpaRepository<PaymentGatewayCustomer, UUID> {
}
