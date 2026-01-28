package org.wita.erp.domain.entities.product.dtos;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequestDTO(@NotBlank String name) {
}
