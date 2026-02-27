package org.wita.erp.domain.entities.transaction.dtos;

import jakarta.validation.constraints.NotBlank;

public record DeleteTransactionRequestDTO(
        @NotBlank String reason
) {}
