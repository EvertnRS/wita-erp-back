package org.wita.erp.domain.entities.transaction.purchase.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.wita.erp.domain.entities.transaction.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdatePurchaseRequestDTO(UUID buyer,
                              UUID supplier,
                              UUID companyPaymentType,
                              UUID movementReason,
                              String transactionCode,
                              BigDecimal value,
                              PaymentStatus paymentStatus,
                              @Min(1) @Max(48) Integer installments,
                              String description) {
}
