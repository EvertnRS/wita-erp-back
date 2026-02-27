package org.wita.erp.domain.entities.payment.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record PaymentCheckoutDTO(
        @Schema(description = "Payment's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID payment,
        @Schema(description = "URL to redirect the customer for completing the payment", example = "https://payment-gateway.com/checkout/123e4567-e89b-12d3-a456-426614174000")
        String checkoutUrl
) {
}
