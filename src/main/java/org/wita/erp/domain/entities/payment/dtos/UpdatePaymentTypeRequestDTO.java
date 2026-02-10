package org.wita.erp.domain.entities.payment.dtos;

import org.wita.erp.domain.entities.payment.PaymentMethod;

public record UpdatePaymentTypeRequestDTO(PaymentMethod paymentMethod,
                                          Boolean isImmediate,
                                          Boolean allowsInstallments) {
}
