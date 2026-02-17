package org.wita.erp.domain.entities.transaction.order.dtos;

import jakarta.validation.constraints.NotBlank;

public record DeleteOrderRequestDTO(
        @NotBlank String reason
) {}
