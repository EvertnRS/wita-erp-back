package org.wita.erp.domain.entities.order.dtos;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record UpdateOrderRequestDTO(BigDecimal discount,
                                    UUID seller,
                                    UUID customer,
                                    UUID paymentType,
                                    String transactionCode,
                                    String description,
                                    UUID movementReason,
                                    Set<ProductOrderRequestDTO> products) {
}
