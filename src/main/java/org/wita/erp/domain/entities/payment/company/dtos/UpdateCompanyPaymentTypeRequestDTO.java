package org.wita.erp.domain.entities.payment.company.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateCompanyPaymentTypeRequestDTO(
        @Schema(description = "Indicates if the payment type is immediate or not", example = "true")
        Boolean isImmediate,
        @Schema(description = "Indicates if the payment type allows installments or not", example = "false")
        Boolean allowsInstallments,
        @Schema(description = "New company's bank code for this payment type", example = "001")
        String bankCode,
        @Schema(description = "New company's agency number for this payment type", example = "1234")
        String agencyNumber,
        @Schema(description = "New company's account number for this payment type", example = "12345678")
        String accountNumber,
        @Schema(description = "New company's last four digits of the card for this payment type", example = "1234")
        String lastFourDigits,
        @Schema(description = "New company's card brand for this payment type", example = "Visa")
        String brand,
        @Schema(description = "New company's closing day for this payment type", example = "15")
        Integer closingDay) {

}
