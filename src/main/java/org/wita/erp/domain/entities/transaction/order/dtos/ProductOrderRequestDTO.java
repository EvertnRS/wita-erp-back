package org.wita.erp.domain.entities.transaction.order.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductOrderRequestDTO(
        @Schema(description = "Product's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull UUID productId,
        @Schema(description = "Quantity of the product to be ordered", example = "10")
        @NotNull Integer quantity
        ) {
}
