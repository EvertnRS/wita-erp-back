package org.wita.erp.domain.entities.transaction.order.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.wita.erp.domain.entities.payment.customer.dto.CustomerPaymentTypeDTO;
import org.wita.erp.domain.entities.transaction.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReceivableDTO(
        @Schema(description = "Receivable's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "Receivable's value", example = "100.00")
        BigDecimal value,
        @Schema(description = "Receivable's due date", example = "2024-12-31")
        LocalDate dueDate,
        @Schema(description = "Receivable's payment status", example = "PENDING")
        PaymentStatus paymentStatus,
        @Schema(description = "Receivable's payment type", example = "Credit Card")
        CustomerPaymentTypeDTO customerPaymentType,
        @Schema(description = "Receivable's related order ID", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID order,
        @Schema(description = "Number of installments for the receivable", example = "3")
        Integer installment,
        @Schema(description = "Idicates if the receivable is active or not", example = "true")
        Boolean active,
        @Schema(description = "Day the receivable was created", example = "2024-01-01T12:00:00")
        LocalDateTime createdAt
) {
}
