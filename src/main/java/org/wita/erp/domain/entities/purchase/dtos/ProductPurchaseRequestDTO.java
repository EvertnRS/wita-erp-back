package org.wita.erp.domain.entities.purchase.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductPurchaseRequestDTO(
        @NotNull UUID product,
        @NotNull Integer quantity
        ) {
}
