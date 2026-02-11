package org.wita.erp.domain.entities.product.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequestDTO(
        String name,
        BigDecimal price,
        @Positive Integer minQuantity,
        @Positive @Min(0) Integer quantityInStock,
        UUID category,
        UUID supplier
) {
}
