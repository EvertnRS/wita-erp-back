package org.wita.erp.domain.entities.purchase.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdatePurchaseRequestDTO(BigDecimal value,
                                       UUID supplier,
                                       UUID paymentType,
                                       String transactionCode,
                                       String description) {
}
