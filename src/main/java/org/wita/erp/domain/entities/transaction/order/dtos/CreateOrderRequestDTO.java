package org.wita.erp.domain.entities.transaction.order.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record CreateOrderRequestDTO(
        @Schema(description = "Order's discount, if applicable", example = "1.00")
        @NotNull @Min(0) BigDecimal discount,
        @Schema(description = "Number of installments for the order, if applicable", example = "12")
        @Min(1) @Max(48) Integer installments,
        @Schema(description = "Seller's unique identifier for the order", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull UUID seller,
        @Schema(description = "Customer payment type's unique identifier for the order", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull UUID customerPaymentType,
        @Schema(description = "Transaction code for the order", example = "1234567890")
        @NotNull String transactionCode,
        @Schema(description = "Additional description for the order", example = "Order for customer XYZ")
        String description,
        @Schema(description = "Movement reason's unique identifier for the order", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull UUID movementReason,
        @NotNull Set<ProductOrderRequestDTO> products) {
}
