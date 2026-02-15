package org.wita.erp.domain.entities.payment.customer.dto;

import jakarta.validation.constraints.NotBlank;

public record DeleteCustomerPaymentTypeRequestDTO(
        @NotBlank String reason
) {}
