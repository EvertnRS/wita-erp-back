package org.wita.erp.domain.entities.transaction.order.dtos;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.wita.erp.domain.entities.status.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateReceivableRequestDTO(@FutureOrPresent LocalDate dueDate,
                                         PaymentStatus paymentStatus,
                                         UUID order) {
}
