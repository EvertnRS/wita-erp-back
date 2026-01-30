package org.wita.erp.domain.entities.payment.dtos;

import jakarta.validation.constraints.Positive;
import org.wita.erp.domain.entities.payment.PaymentMethod;

public record UpdatePaymentTypeRequestDTO(PaymentMethod paymentMethod,
                                          Boolean isImmediate,
                                          Boolean allowsInstallments,
                                          @Positive Integer maxInstallments) {
}
