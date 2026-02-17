package org.wita.erp.domain.entities.payment.company.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateCompanyPaymentTypeRequestDTO(
        @Schema(description = "Indicates if the payment type is immediate or not", example = "true")
        @NotNull Boolean isImmediate,
        @Schema(description = "Indicates if the payment type allows installments or not", example = "false")
        @NotNull Boolean allowsInstallments,
        @Schema(description = "Company's bank code for this payment type", example = "001")
        @NotNull String bankCode,
        @Schema(description = "Company's agency number for this payment type", example = "1234")
        @NotNull String agencyNumber,
        @Schema(description = "Company's account number for this payment type", example = "12345678")
        @NotNull String accountNumber,
        @Schema(description = "Company's credit card last four digits for this payment type", example = "1234")
        String lastFourDigits,
        @Schema(description = "Company's credit card brand for this payment type", example = "Visa")
        String brand,
        @Schema(description = "Company's credit card closing day for this payment type", example = "15")
        Integer closingDay) {
}
