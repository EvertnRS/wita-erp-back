package org.wita.erp.domain.entities.transaction.order.dtos;

import jakarta.validation.constraints.NotNull;
import org.wita.erp.domain.entities.status.PaymentStatus;

import java.util.UUID;

public record CreateReceivableRequestDTO(@NotNull PaymentStatus paymentStatus,
                                         @NotNull UUID order) {
}
