package org.wita.erp.domain.entities.product.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateCategoryRequestDTO(
        @Schema(description = "New category name", example = "Electronics")
        String name
) {
}
