package org.wita.erp.domain.entities.transaction.purchase.dtos;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record UpdatePurchaseRequestDTO(BigDecimal value,
                                       UUID buyer,
                                       UUID supplier,
                                       UUID paymentType,
                                       UUID movementReason,
                                       String transactionCode,
                                       String description,
                                       Set<ProductPurchaseRequestDTO> products) {
}
