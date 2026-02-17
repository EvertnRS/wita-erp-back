package org.wita.erp.domain.entities.payment.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateCustomerPaymentTypeRequestDTO(
        @Schema(description = "Idicates if the payment type is immediate or not", example = "true")
        @NotNull Boolean isImmediate,
        @Schema(description = "Idicates if the payment type allows installments or not", example = "false")
        @NotNull Boolean allowsInstallments,
        @Schema(description = "Idicates if the payment type allows refunds or not", example = "true")
        @NotNull Boolean supportsRefunds,
        @Schema(description = "ID of the customer associated with this payment type", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull UUID customer //FIXME: Cada Forma de pagamento deve estar associada a um cliente? então se dois clientes pagarem com Crédito, teremos duas formas de pagamento iguais, mas associadas a clientes diferentes?
) {
}
