package org.wita.erp.domain.entities.payment.customer.dto;


import jakarta.validation.constraints.Positive;
import org.wita.erp.domain.entities.payment.PaymentMethod;
import org.wita.erp.domain.entities.payment.customer.CustomerPaymentType;

public record UpdateCustomerPaymentTypeRequestDTO(PaymentMethod paymentMethod,
                                                  Boolean isImmediate,
                                                  Boolean allowsInstallments,
                                                  @Positive Integer maxInstallments,
                                                  Boolean supportsRefunds) {
}
