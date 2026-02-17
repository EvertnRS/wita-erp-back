package org.wita.erp.domain.entities.stock.dtos;


import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateMovementReasonRequestDTO(
        @Schema(description = "New reason for the stock movement", example = "Stock Adjustment")
        String reason
) {
}
