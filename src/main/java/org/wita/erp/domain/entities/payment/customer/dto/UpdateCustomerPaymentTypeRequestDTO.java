package org.wita.erp.domain.entities.payment.customer.dto;


import org.wita.erp.domain.entities.payment.PaymentMethod;

public record UpdateCustomerPaymentTypeRequestDTO(PaymentMethod paymentMethod,
                                                  Boolean isImmediate,
                                                  Boolean allowsInstallments,
                                                  Boolean supportsRefunds) {
}
