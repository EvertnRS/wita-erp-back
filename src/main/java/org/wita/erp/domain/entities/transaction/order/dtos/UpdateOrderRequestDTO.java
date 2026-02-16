package org.wita.erp.domain.entities.transaction.order.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateOrderRequestDTO(
        @Schema(description = "New order value", example = "100.00")
        BigDecimal value,
        @Schema(description = "New number of installments", example = "12")
        @Min(1) @Max(48) Integer installments,
        @Schema(description = "New discount applied to the order", example = "1.00")
        BigDecimal discount,
        @Schema(description = "New unique identifier of the seller associated with the order", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID seller,
        @Schema(description = "New unique identifier of the payment type associated with the order", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID customerPaymentType,
        @Schema(description = "New transaction code for the order", example = "1234567890")
        String transactionCode,
        @Schema(description = "New description for the order", example = "Order for customer XYZ")
        String description) {
}
