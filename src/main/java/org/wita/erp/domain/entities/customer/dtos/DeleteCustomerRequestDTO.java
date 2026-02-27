package org.wita.erp.domain.entities.customer.dtos;

import jakarta.validation.constraints.NotBlank;

public record DeleteCustomerRequestDTO(
        @NotBlank String reason
) {}
