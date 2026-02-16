package org.wita.erp.domain.entities.product.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.wita.erp.domain.entities.supplier.dtos.SupplierDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductDTO(
        @Schema(description = "Product's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "Product's name", example = "Wireless Mouse")
        String name,
        @Schema(description = "Product's price", example = "29.99")
        BigDecimal price,
        @Schema(description = "Indicates, if the product has a discount, the value of the discount", example = "1.00")
        BigDecimal discount,
        @Schema(description = "Minimum quantity required to apply the discount", example = "5")
        int minQuantityToDiscount,
        @Schema(description = "Minimum quantity for the system to consider the product as low in stock", example = "10")
        Integer minQuantity,
        @Schema(description = "Current quantity of the product in stock", example = "150")
        Integer quantityInStock,
        CategoryDTO category,
        SupplierDTO supplier,
        @Schema(description = "Timestamp when the product was created", example = "2024-06-01T12:00:00")
        LocalDateTime createdAt,
        @Schema(description = "Indicates if the product is active", example = "true")
        Boolean active) {
}
