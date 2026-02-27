package org.wita.erp.domain.entities.transaction.order.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDTO(
        @Schema(description = "Product's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID productId,
        @Schema(description = "Product's name", example = "Laptop")
        String ProductName,
        @Schema(description = "Product's unit price", example = "1000.00")
        BigDecimal unitPrice,
        @Schema(description = "Quantity of the product in the order", example = "2")
        Integer quantity,
        @Schema(description = "Total price for the product in the order", example = "2000.00")
        BigDecimal total) {
}
