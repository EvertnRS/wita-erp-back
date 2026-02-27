package org.wita.erp.domain.entities.transaction.purchase.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import org.wita.erp.domain.entities.transaction.PaymentStatus;

import java.time.LocalDate;

public record UpdatePayableRequestDTO(
        @Schema(description = "New due date for the payable", example = "2024-12-31")
        @FutureOrPresent LocalDate dueDate,
        @Schema(description = "New payment status for the payable", example = "PENDING")
        PaymentStatus paymentStatus) {
}
