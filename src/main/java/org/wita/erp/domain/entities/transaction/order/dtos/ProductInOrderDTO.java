package org.wita.erp.domain.entities.transaction.order.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductInOrderDTO(
        @NotNull ProductOrderRequestDTO product,
        @Schema(description = "Unique identifier of the movement reason to this change in the order", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull UUID movementReason
) {
}
