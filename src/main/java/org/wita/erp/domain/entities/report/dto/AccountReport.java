package org.wita.erp.domain.entities.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.wita.erp.domain.entities.transaction.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AccountReport(
        @Schema(description = "Unique identifier of the account report entry", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "Description of the account report entry", example = "Payment for invoice #12345")
        BigDecimal value,
        @Schema(description = "Due date of the payment", example = "2024-12-31")
        LocalDate dueDate,
        @Schema(description = "Status of the payment", example = "PENDING")
        PaymentStatus status,
        @Schema(description = "Type of the account report entry", example = "PAYABLE")
        String type) {

}