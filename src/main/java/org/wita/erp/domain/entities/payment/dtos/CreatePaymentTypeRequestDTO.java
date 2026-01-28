package org.wita.erp.domain.entities.payment.dtos;

public record CreatePaymentTypeRequestDTO(String name, Boolean isImmediate, Boolean allowsInstallments, Integer maxInstallments) {
}
