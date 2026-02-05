package org.wita.erp.domain.entities.transaction.purchase.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record CreateReplacementPurchaseRequestDTO(
                                       @NotNull String transactionCode,
                                       String description,
                                       @NotNull UUID buyer,
                                       @NotNull UUID supplier,
                                       @NotNull UUID paymentType,
                                       @NotNull UUID movementReason,
                                       @NotNull Set<ProductPurchaseRequestDTO> products) {
}
