package org.wita.erp.domain.entities.purchase.dtos;

import jakarta.validation.constraints.NotNull;
import org.wita.erp.domain.entities.purchase.dtos.ProductPurchaseRequestDTO;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record CreatePurchaseRequestDTO(@NotNull BigDecimal value,
                                       @NotNull String transactionCode,
                                       String description,
                                       @NotNull UUID buyer,
                                       @NotNull UUID supplier,
                                       @NotNull UUID paymentType,
                                       @NotNull UUID movementReason,
                                       Set<ProductPurchaseRequestDTO> products) {
}
