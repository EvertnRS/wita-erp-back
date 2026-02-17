package org.wita.erp.domain.entities.product.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequestDTO(
        @Schema(description = "New name of the product", example = "Wita Mouse")
        String name,
        @Schema(description = "New price of the product", example = "29.99")
        BigDecimal price,
        @Schema(description = "New minimum quantity for the system to consider the product as low in stock", example = "10")
        @Positive Integer minQuantity,
        @Schema(description = "New current quantity of the product in stock", example = "150")
        @Positive @Min(0) Integer quantityInStock,
        @Schema(description = "New category ID to which the product belongs", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID category,
        @Schema(description = "New supplier ID from which the product is sourced", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID supplier
) {
}
