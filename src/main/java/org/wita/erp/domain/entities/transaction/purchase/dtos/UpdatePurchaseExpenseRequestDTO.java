package org.wita.erp.domain.entities.transaction.purchase.dtos;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdatePurchaseExpenseRequestDTO(
                                                @Positive BigDecimal value,
                                                UUID buyer,
                                              UUID supplier,
                                              UUID paymentType,
                                              String transactionCode,
                                              String description) {
}
