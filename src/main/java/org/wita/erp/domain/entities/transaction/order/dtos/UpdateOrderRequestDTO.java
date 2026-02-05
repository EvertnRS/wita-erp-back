package org.wita.erp.domain.entities.transaction.order.dtos;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record UpdateOrderRequestDTO(BigDecimal value,
                                    BigDecimal discount,
                                    UUID seller,
                                    UUID customer,
                                    UUID paymentType,
                                    UUID movementReason,
                                    String transactionCode,
                                    String description,
                                    Set<ProductOrderRequestDTO> products) {
}
