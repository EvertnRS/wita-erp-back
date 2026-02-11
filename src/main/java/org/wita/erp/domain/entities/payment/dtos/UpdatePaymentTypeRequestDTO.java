package org.wita.erp.domain.entities.payment.dtos;

public record UpdatePaymentTypeRequestDTO(
                                          Boolean isImmediate,
                                          Boolean allowsInstallments) {
}
