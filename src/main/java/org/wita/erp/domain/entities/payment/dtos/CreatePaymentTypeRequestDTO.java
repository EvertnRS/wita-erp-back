package org.wita.erp.domain.entities.payment.dtos;

import jakarta.validation.constraints.NotNull;
import org.wita.erp.domain.entities.payment.PaymentMethod;

public record CreatePaymentTypeRequestDTO(@NotNull PaymentMethod paymentMethod,
                                          @NotNull Boolean isImmediate,
                                          @NotNull Boolean allowsInstallments) {
}
