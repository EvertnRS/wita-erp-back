package org.wita.erp.domain.entities.product.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequestDTO(
        @Schema(description = "Product's category name", example = "Electronics")
        @NotBlank String name
) {
}
