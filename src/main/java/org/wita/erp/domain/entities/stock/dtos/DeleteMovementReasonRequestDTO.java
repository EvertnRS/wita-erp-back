package org.wita.erp.domain.entities.stock.dtos;

import jakarta.validation.constraints.NotBlank;

public record DeleteMovementReasonRequestDTO(
        @NotBlank String reason
) {}
