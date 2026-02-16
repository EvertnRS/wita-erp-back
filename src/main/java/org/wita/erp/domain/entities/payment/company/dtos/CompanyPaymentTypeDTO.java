package org.wita.erp.domain.entities.payment.company.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record CompanyPaymentTypeDTO(
        @Schema(description = "Company's payment type unique identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "Indicates if the payment type is immediate or not", example = "true")
        Boolean isImmediate,
        @Schema(description = "Indicates if the payment type allows installments or not", example = "false")
        Boolean allowInstallments,
        @Schema(description = "Company's bank code for this payment type", example = "001")
        String bankCode,
        @Schema(description = "Company's agency number for this payment type", example = "1234")
        String lastFourDigits,
        @Schema(description = "Company's brand for this payment type", example = "Visa")
        String brand,
        @Schema(description = "Company's closing day for this payment type", example = "15")
        Integer closingDay,
        @Schema(description = "Indicates if the payment type is active or not", example = "true")
        Boolean active) {
}
