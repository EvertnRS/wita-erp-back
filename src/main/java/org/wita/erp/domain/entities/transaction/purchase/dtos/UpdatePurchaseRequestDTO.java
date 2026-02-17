package org.wita.erp.domain.entities.transaction.purchase.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.wita.erp.domain.entities.transaction.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdatePurchaseRequestDTO(
        @Schema(description = "New buyer's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID buyer,
        @Schema(description = "New supplier's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID supplier,
        @Schema(description = "New company payment type's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID companyPaymentType,
        @Schema(description = "New unique code for the transaction", example = "TR1234567890")
        String transactionCode,
        @Schema(description = "New total value of the purchase", example = "100.00")
        BigDecimal value,
        @Schema(description = "New number of installments for the purchase", example = "3")
        @Min(1) @Max(48) Integer installments,
        @Schema(description = "Additional details about the purchase", example = "This is an updated description for the purchase.")
        String description,
        @Schema(description = "New movement reason's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID movementReason,
        @Schema(description = "New payment status for purchase", example = "CANCELED")
        PaymentStatus paymentStatus) {
}
