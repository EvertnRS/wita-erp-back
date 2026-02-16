package org.wita.erp.domain.entities.payment.customer.dto;


import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateCustomerPaymentTypeRequestDTO(
        @Schema(description = "Indicates if the payment type is immediate or not", example = "true")
        Boolean isImmediate,
        @Schema(description = "Indicates if the payment type allows installments or not", example = "false")
        Boolean allowsInstallments,
        @Schema(description = "Indicates if the payment type allows refunds or not", example = "true")
        Boolean supportsRefunds) {
}
