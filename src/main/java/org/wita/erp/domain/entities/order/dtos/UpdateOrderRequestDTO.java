package org.wita.erp.domain.entities.order.dtos;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record UpdateOrderRequestDTO(BigDecimal value,
                                    BigDecimal discount,
                                    UUID seller,
                                    UUID customer,
                                    UUID paymentType,
                                    String transactionCode,
                                    Set<ProductOrderRequestDTO> products) {
}
