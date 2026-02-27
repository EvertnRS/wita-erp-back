package org.wita.erp.domain.entities.transaction.purchase.dtos;

import jakarta.validation.constraints.NotBlank;

public record DeletePurchaseRequestDTO(
        @NotBlank String reason
) {}
