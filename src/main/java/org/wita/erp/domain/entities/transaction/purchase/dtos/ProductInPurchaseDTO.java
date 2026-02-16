package org.wita.erp.domain.entities.transaction.purchase.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductInPurchaseDTO(
        @NotNull ProductPurchaseRequestDTO product,
        @Schema(description = "Movement reason’s unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull UUID movementReason
) {
}
