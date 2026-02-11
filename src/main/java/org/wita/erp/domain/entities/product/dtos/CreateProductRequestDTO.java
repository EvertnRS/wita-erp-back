package org.wita.erp.domain.entities.product.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequestDTO(
        @NotBlank String name,
        @NotNull @Positive BigDecimal price,
        @NotNull @Min(0) BigDecimal discount,
        @NotNull @Min(0) Integer minQuantityForDiscount,
        @NotNull @Positive Integer minQuantity,
        @NotNull @Positive Integer quantityInStock,
        @NotNull UUID category,
        @NotNull UUID supplier
) {
}
