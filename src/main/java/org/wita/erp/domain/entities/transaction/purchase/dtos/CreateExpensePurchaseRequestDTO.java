package org.wita.erp.domain.entities.transaction.purchase.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateExpensePurchaseRequestDTO(
        @Schema(description = "Total value of the purchase", example = "1500.00")
        @NotNull BigDecimal value,
        @Schema(description = "Transaction code for the purchase", example = "TR1234567890")
        @NotNull String transactionCode,
        @Schema(description = "Number of installments for the purchase", example = "3")
        @Min(1) @Max(48) Integer installments,
        @Schema(description = "Additional details about the purchase", example = "This purchase is for office supplies.")
        String description,
        @NotNull UUID buyer,
        @Schema(description = "Supplier’s unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull UUID supplier,
        @Schema(description = "Company payment type’s unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull UUID companyPaymentType) {
}
