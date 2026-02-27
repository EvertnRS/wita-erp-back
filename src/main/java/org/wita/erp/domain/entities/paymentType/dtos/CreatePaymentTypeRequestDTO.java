package org.wita.erp.domain.entities.paymentType.dtos;

import jakarta.validation.constraints.NotNull;

public record CreatePaymentTypeRequestDTO(
                                          @NotNull Boolean isImmediate,
                                          @NotNull Boolean allowsInstallments) {
}
