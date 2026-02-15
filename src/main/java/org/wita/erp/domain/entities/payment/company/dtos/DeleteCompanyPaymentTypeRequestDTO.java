package org.wita.erp.domain.entities.payment.company.dtos;

import jakarta.validation.constraints.NotBlank;

public record DeleteCompanyPaymentTypeRequestDTO(
        @NotBlank String reason
) {}
