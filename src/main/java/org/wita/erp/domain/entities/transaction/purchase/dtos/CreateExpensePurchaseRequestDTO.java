package org.wita.erp.domain.entities.transaction.purchase.dtos;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateExpensePurchaseRequestDTO(
                                        @NotNull BigDecimal value,
                                       @NotNull String transactionCode,
                                       String description,
                                       @NotNull UUID buyer,
                                       @NotNull UUID supplier,
                                       @NotNull UUID paymentType) {
}
