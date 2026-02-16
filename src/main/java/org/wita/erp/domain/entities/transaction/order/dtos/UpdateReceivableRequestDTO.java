package org.wita.erp.domain.entities.transaction.order.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import org.wita.erp.domain.entities.status.PaymentStatus;

import java.time.LocalDate;

public record UpdateReceivableRequestDTO(
        @Schema(description = "New due date for the receivable", example = "2024-12-31")
        @FutureOrPresent LocalDate dueDate,
        @Schema(description = "New payment status for the receivable", example = "PENDING")
        PaymentStatus paymentStatus) {
}
