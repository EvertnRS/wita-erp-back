package org.wita.erp.domain.entities.payment.dtos;

public record UpdatePaymentTypeRequestDTO(String name, Boolean isImmediate, Boolean allowsInstallments, Integer maxInstallments) {
}
