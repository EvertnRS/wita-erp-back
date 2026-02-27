package org.wita.erp.domain.entities.paymentType.dtos;

public record UpdatePaymentTypeRequestDTO(
                                          Boolean isImmediate,
                                          Boolean allowsInstallments) {
}
