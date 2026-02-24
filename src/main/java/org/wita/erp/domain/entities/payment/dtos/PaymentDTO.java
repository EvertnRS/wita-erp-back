package org.wita.erp.domain.entities.payment.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.wita.erp.domain.entities.payment.PaymentGateway;
import org.wita.erp.domain.entities.transaction.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentDTO(
        @Schema(description = "Payment's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "Receivable's unique identifier associated with the payment", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID receivableId,
        @Schema(description = "Current status of the payment", example = "PENDING")
        PaymentStatus status,
        @Schema(description = "Amount paid in the transaction", example = "150.00")
        BigDecimal amount,
        @Schema(description = "Currency used in the payment", example = "USD")
        String currency,
        @Schema(description = "Payment gateway used for the transaction", example = "STRIPE")
        PaymentGateway gateway,
        @Schema(description = "Number of attempts made to process the payment", example = "1")
        Integer attempts,
        @Schema(description = "Timestamp of the last event related to the payment", example = "2024-06-01T12:00:00")
        LocalDateTime createdAt,
        @Schema(description = "Timestamp when the payment was successfully processed", example = "2024-06-01T12:05:00")
        LocalDateTime paidAt
) {}
