package org.wita.erp.domain.entities.product.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record CategoryDTO(
        @Schema(description = "Product's category unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "Product's category name", example = "Electronics")
        String name,
        @Schema(description = "Indicates if the category is active", example = "true")
        Boolean active) {
}
