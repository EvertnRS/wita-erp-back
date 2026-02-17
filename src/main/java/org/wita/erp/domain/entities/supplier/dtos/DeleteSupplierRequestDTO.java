package org.wita.erp.domain.entities.supplier.dtos;

import jakarta.validation.constraints.NotBlank;

public record DeleteSupplierRequestDTO(
        @NotBlank String reason
) {}
