package org.wita.erp.domain.entities.transaction.purchase.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record CreateReplacementPurchaseRequestDTO(
        @Schema(description = "Unique code for the transaction", example = "TR1234567890")
        @NotNull String transactionCode,
        @Schema(description = "Number of installments for the purchase", example = "3")
        Integer installments,
        @Schema(description = "Additional details about the purchase", example = "This is a replacement for a previous order.")
        String description,
        @Schema(description = "Buyer’s unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull UUID buyer,
        @Schema(description = "Supplier’s unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull UUID supplier,
        @Schema(description = "Company payment type’s unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull UUID companyPaymentType,
        @Schema(description = "Movement reason’s unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull UUID movementReason,
        @NotNull Set<ProductPurchaseRequestDTO> products) {
}
