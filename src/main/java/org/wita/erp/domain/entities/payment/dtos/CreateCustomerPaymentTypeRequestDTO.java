package org.wita.erp.domain.entities.payment.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.wita.erp.domain.entities.payment.PaymentMethod;

public record CreateCustomerPaymentTypeRequestDTO(@NotNull PaymentMethod paymentMethod,
                                                  @NotNull Boolean isImmediate,
                                                  @NotNull Boolean allowsInstallments,
                                                  @Positive Integer maxInstallments,
                                                  @NotNull Boolean supportsRefunds) {
}
