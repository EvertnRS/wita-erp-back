package org.wita.erp.domain.entities.transaction.order.dtos;

import org.wita.erp.domain.entities.payment.dtos.PaymentTypeDTO;
import org.wita.erp.domain.entities.status.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReceivableDTO(UUID id, BigDecimal value, LocalDate dueDate, PaymentStatus paymentStatus, PaymentTypeDTO paymentType, UUID order,
                            Integer installment, Boolean active, LocalDateTime createdAt) {
}
