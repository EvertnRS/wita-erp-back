package org.wita.erp.domain.entities.purchase.dtos;

import jakarta.validation.constraints.NotNull;
import org.wita.erp.domain.entities.order.dtos.ProductOrderRequestDTO;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record CreatePurchaseRequestDTO(@NotNull BigDecimal value,
                                       @NotNull UUID supplier,
                                       @NotNull UUID paymentType,
                                       @NotNull String transactionCode,
                                       String description,
                                       @NotNull UUID movementReason,
                                       Set<ProductOrderRequestDTO> products) {
}
