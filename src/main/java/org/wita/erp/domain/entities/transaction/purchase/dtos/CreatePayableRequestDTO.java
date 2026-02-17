package org.wita.erp.domain.entities.transaction.purchase.dtos;

import jakarta.validation.constraints.NotNull;
import org.wita.erp.domain.entities.transaction.PaymentStatus;

import java.util.UUID;

public record CreatePayableRequestDTO(@NotNull PaymentStatus paymentStatus,
                                      @NotNull UUID purchase) {
}
