package org.wita.erp.domain.entities.payment.dtos;

import jakarta.validation.constraints.NotNull;

public record UpdatePaymentTypeRequestDTO(@NotNull String name,
                                          @NotNull Boolean isImmediate,
                                          @NotNull Boolean allowsInstallments,
                                          Integer maxInstallments) {
}
