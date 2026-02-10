package org.wita.erp.domain.entities.transaction.order.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateOrderRequestDTO(BigDecimal value,
                                    Integer installments,
                                    BigDecimal discount,
                                    UUID seller,
                                    UUID paymentType,
                                    String transactionCode,
                                    String description) {
}
