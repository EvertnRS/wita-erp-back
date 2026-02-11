package org.wita.erp.domain.entities.transaction.purchase.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateExpensePurchaseRequestDTO(
        @NotNull BigDecimal value,
        @NotNull String transactionCode,
        @Min(1) @Max(48) Integer installments,
        String description,
        @NotNull UUID buyer,
        @NotNull UUID supplier,
        @NotNull UUID companyPaymentType) {
}
