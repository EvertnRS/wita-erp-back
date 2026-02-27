package org.wita.erp.domain.entities.payment.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.wita.erp.domain.entities.payment.PaymentGateway;

public record CreatePaymentRequestDTO(
        @Schema(description = "Payment gateway used for the transaction", example = "STRIPE")
        PaymentGateway gateway
) {}
