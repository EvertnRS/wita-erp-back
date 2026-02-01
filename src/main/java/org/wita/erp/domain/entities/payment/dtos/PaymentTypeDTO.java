package org.wita.erp.domain.entities.payment.dtos;

import java.util.UUID;

public record PaymentTypeDTO(UUID id, String paymentMethod) {
}
