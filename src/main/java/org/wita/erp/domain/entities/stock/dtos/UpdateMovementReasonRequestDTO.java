package org.wita.erp.domain.entities.stock.dtos;

import jakarta.validation.constraints.NotBlank;

public record UpdateMovementReasonRequestDTO(@NotBlank String reason) {
}
