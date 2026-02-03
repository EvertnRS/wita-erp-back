package org.wita.erp.domain.entities.order.dtos;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record CreateOrderRequestDTO(BigDecimal discount,
                                    @NotNull UUID seller,
                                    @NotNull UUID customer,
                                    @NotNull UUID paymentType,
                                    @NotNull String transactionCode,
                                    @NotNull UUID movementReason,
                                    @NotNull Set<ProductOrderRequestDTO> products) {
}
