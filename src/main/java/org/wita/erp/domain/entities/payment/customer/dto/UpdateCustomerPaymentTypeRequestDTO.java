package org.wita.erp.domain.entities.payment.customer.dto;


import org.wita.erp.domain.entities.payment.PaymentMethod;

import java.util.UUID;

public record UpdateCustomerPaymentTypeRequestDTO(PaymentMethod paymentMethod,
                                                  Boolean isImmediate,
                                                  Boolean allowsInstallments,
                                                  Boolean supportsRefunds,
                                                  UUID customer) {
}
