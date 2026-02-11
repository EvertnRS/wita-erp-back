package org.wita.erp.domain.entities.transaction.purchase.dtos;

import org.wita.erp.domain.entities.payment.company.dtos.CompanyPaymentTypeDTO;
import org.wita.erp.domain.entities.status.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PayableDTO(UUID id, BigDecimal value, LocalDate dueDate, PaymentStatus paymentStatus, CompanyPaymentTypeDTO companyPaymentType, UUID purchase,
                         Integer installment, Boolean active, LocalDateTime createdAt) {
}
