package org.wita.erp.domain.product.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequestDTO(
        @NotBlank String name,
        @NotNull BigDecimal price,
        @NotNull @Positive Integer minQuantity,
        @NotNull @Positive Integer quantityInStock,
        @NotNull UUID category
) {
}
