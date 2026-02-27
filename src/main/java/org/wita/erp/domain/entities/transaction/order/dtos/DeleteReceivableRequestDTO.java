package org.wita.erp.domain.entities.transaction.order.dtos;

import jakarta.validation.constraints.NotBlank;

public record DeleteReceivableRequestDTO(
        @NotBlank String reason
) {}
