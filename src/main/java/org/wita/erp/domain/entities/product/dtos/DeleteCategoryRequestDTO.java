package org.wita.erp.domain.entities.product.dtos;

import jakarta.validation.constraints.NotBlank;

public record DeleteCategoryRequestDTO(
        @NotBlank String reason
) {}
