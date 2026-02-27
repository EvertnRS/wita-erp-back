package org.wita.erp.domain.entities.paymentType.company.dtos;

import jakarta.validation.constraints.NotBlank;

public record DeleteCompanyPaymentTypeRequestDTO(
        @NotBlank String reason
) {}
