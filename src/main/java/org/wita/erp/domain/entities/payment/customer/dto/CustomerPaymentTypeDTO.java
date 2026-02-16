package org.wita.erp.domain.entities.payment.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.wita.erp.domain.entities.customer.Customer;
import org.wita.erp.domain.entities.customer.dtos.CustomerDTO;

import java.util.UUID;

public record CustomerPaymentTypeDTO(
        @Schema(description = "Customer's payment type unique identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "Indicates if the payment type is immediate or not", example = "true")
        Boolean isImmediate,
        @Schema(description = "Indicates if the payment type allows installments or not", example = "false")
        Boolean allowsInstallments,
        @Schema(description = "Indicates if the payment type is immediate or not", example = "true")
        Boolean supportsRefunds,
        CustomerDTO customer,
        @Schema(description = "Indicates if the payment type is active or not", example = "true")
        Boolean active) {
}
