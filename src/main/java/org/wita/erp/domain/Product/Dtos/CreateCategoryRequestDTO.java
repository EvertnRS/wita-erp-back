package org.wita.erp.domain.Product.Dtos;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequestDTO(@NotBlank String name) {
}
