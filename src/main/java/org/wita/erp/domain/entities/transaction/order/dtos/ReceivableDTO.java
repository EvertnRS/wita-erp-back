package org.wita.erp.domain.entities.transaction.order.dtos;

import org.wita.erp.domain.entities.payment.customer.dto.CustomerPaymentTypeDTO;
import org.wita.erp.domain.entities.transaction.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReceivableDTO(UUID id, BigDecimal value, LocalDate dueDate, PaymentStatus paymentStatus, CustomerPaymentTypeDTO customerPaymentType, UUID order,
                            Integer installment, Boolean active, LocalDateTime createdAt) {
}
