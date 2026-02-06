package org.wita.erp.domain.entities.order.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddProductInOrderDTO(
        @NotNull ProductOrderRequestDTO product,
        @NotNull UUID movementReason
        ) {
}
