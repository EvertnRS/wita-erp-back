package org.wita.erp.domain.entities.product.dtos;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequestDTO(
        String name,
        BigDecimal price,
        @Positive Integer minQuantity,
        @Positive Integer quantityInStock,
        UUID category,
        UUID supplier
) {
}
