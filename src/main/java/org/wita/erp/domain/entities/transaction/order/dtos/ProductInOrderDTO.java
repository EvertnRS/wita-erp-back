package org.wita.erp.domain.entities.transaction.order.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductInOrderDTO(
        @NotNull ProductOrderRequestDTO product,
        @NotNull UUID movementReason
) {
}
