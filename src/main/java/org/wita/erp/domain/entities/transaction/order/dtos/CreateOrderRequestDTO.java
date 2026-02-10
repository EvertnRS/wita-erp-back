package org.wita.erp.domain.entities.transaction.order.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record CreateOrderRequestDTO(@NotNull @Min(0) BigDecimal discount,
                                    Integer installments,
                                    @NotNull UUID seller,
                                    @NotNull UUID paymentType,
                                    @NotNull String transactionCode,
                                    String description,
                                    @NotNull UUID movementReason,
                                    @NotNull Set<ProductOrderRequestDTO> products) {
}
