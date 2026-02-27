package org.wita.erp.domain.entities.product.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequestDTO(
        @Schema(description = "Name of the product", example = "Wireless Mouse")
        @NotBlank String name,
        @Schema(description = "Price of the product", example = "29.99")
        @NotNull @Positive BigDecimal price,
        @Schema(description = "Discount on the product (if applicable)", example = "1.00")
        @NotNull @Min(0) BigDecimal discount,
        @Schema(description = "Minimum quantity required to apply the discount", example = "5")
        @NotNull @Min(0) Integer minQuantityForDiscount,
        @Schema(description = "Minimum quantity for the system to consider the product as low in stock", example = "10")
        @NotNull @Positive Integer minQuantity,
        @Schema(description = "Current quantity of the product in stock", example = "150")
        @NotNull @Positive Integer quantityInStock,
        @Schema(description = "ID of the category the product belongs to", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull UUID category,
        @Schema(description = "ID of the supplier providing the product", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull UUID supplier // FIXME: Cada produto deve estar associado a um fornecedor? e se um produto for fornecido por mais de um fornecedor, teremos produtos duplicados no sistema, mas associados a fornecedores diferentes?
) {
}
