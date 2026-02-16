package org.wita.erp.domain.entities.transaction.purchase.dtos;

import jakarta.validation.constraints.FutureOrPresent;
import org.wita.erp.domain.entities.transaction.PaymentStatus;

import java.time.LocalDate;

public record UpdatePayableRequestDTO(@FutureOrPresent LocalDate dueDate,
                                      PaymentStatus paymentStatus) {
}
