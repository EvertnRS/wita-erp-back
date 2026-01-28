package org.wita.erp.domain.entities.purchase.dtos;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePurchaseRequestDTO(@NotNull BigDecimal value,
                                       @NotNull UUID supplier,
                                       @NotNull UUID paymentType) {
}
