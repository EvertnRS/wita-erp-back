package org.wita.erp.domain.entities.stock.dtos;

import jakarta.validation.constraints.NotBlank;

public record DeleteStockRequestDTO(
        @NotBlank String reason
) {}
