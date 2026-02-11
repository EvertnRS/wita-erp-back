package org.wita.erp.domain.entities.payment.company.dtos;

import jakarta.validation.constraints.NotNull;

public record CreateCompanyPaymentTypeRequestDTO(
                                                 @NotNull Boolean isImmediate,
                                                 @NotNull Boolean allowsInstallments,
                                                 @NotNull String bankCode,
                                                 @NotNull String agencyNumber,
                                                 @NotNull String accountNumber,
                                                 String lastFourDigits,
                                                 String brand,
                                                 Integer closingDay) {
}
