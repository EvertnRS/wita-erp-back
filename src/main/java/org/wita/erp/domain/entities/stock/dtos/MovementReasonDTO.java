package org.wita.erp.domain.entities.stock.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record MovementReasonDTO(
        @Schema(description = "Movement reason's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "Reason for the stock movement", example = "Stock Adjustment")
        String reason,
        @Schema(description = "Indicates if the movement reason is active", example = "true")
        Boolean active
) {
}
