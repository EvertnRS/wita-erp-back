package org.wita.erp.domain.entities.transaction.order.dtos;

import jakarta.validation.constraints.FutureOrPresent;
import org.wita.erp.domain.entities.transaction.PaymentStatus;

import java.time.LocalDate;

public record UpdateReceivableRequestDTO(@FutureOrPresent LocalDate dueDate,
                                         PaymentStatus paymentStatus) {
}
