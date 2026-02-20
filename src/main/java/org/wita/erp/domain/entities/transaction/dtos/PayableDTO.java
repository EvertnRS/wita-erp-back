package org.wita.erp.domain.entities.transaction.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.wita.erp.domain.entities.payment.company.dtos.CompanyPaymentTypeDTO;
import org.wita.erp.domain.entities.transaction.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PayableDTO(
        @Schema(description = "Payable's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "Total value of the payable", example = "1500.00")
        BigDecimal value,
        @Schema(description = "Due date for the payable", example = "2024-12-31")
        LocalDate dueDate,
        @Schema(description = "Payment status of the payable", example = "PENDING")
        PaymentStatus paymentStatus,
        CompanyPaymentTypeDTO companyPaymentType,
        @Schema(description = "Purchase's unique identifier associated with the payable", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID purchase,
        @Schema(description = "Number of installments for the payable", example = "3")
        Integer installment,
        @Schema(description = "Indicates whether the payable is active", example = "true")
        Boolean active,
        @Schema(description = "Date when the payable was created", example = "2024-06-30T12:34:56")
        LocalDateTime createdAt) implements AccountsDTO {
}
