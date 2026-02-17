package org.wita.erp.domain.entities.stock.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record UpdateStockRequestDTO(
        @Schema(description = "New Product's unique identifier of the stock movement", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID product,
        @Schema(description = "New product's quantity of the stock movement", example = "10")
        @Positive Integer quantity,
        @Schema(description = "New Reason of the stock movement unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID movementReason,
        @Schema(description = "New Transaction's unique identifier of the stock movement", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID transaction) {
}
