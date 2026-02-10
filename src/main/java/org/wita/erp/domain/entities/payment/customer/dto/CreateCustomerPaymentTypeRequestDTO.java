package org.wita.erp.domain.entities.payment.customer.dto;

import jakarta.validation.constraints.NotNull;
import org.wita.erp.domain.entities.payment.PaymentMethod;

import java.util.UUID;

public record CreateCustomerPaymentTypeRequestDTO(@NotNull PaymentMethod paymentMethod,
                                                  @NotNull Boolean isImmediate,
                                                  @NotNull Boolean allowsInstallments,
                                                  @NotNull Boolean supportsRefunds,
                                                  @NotNull UUID customer) {
}
