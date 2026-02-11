package org.wita.erp.domain.entities.payment.customer.dto;


public record UpdateCustomerPaymentTypeRequestDTO(
                                                  Boolean isImmediate,
                                                  Boolean allowsInstallments,
                                                  Boolean supportsRefunds) {
}
