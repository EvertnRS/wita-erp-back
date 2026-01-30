package org.wita.erp.domain.entities.payment.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.wita.erp.domain.entities.payment.PaymentMethod;

public record CreateCompanyPaymentTypeRequestDTO(@NotNull PaymentMethod paymentMethod,
                                                 @NotNull Boolean isImmediate,
                                                 @NotNull Boolean allowsInstallments,
                                                 @Positive Integer maxInstallments,
                                                 @NotNull String bankCode,
                                                 @NotNull String agencyNumber,
                                                 @NotNull String accountNumber,
                                                 @NotNull String lastFourDigits,
                                                 @NotNull String brand,
                                                 @NotNull Integer closingDay) {
}
