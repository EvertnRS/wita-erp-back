package org.wita.erp.domain.entities.transaction.order.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateOrderRequestDTO(BigDecimal value,
                                    @Min(1) @Max(48) Integer installments,
                                    BigDecimal discount,
                                    UUID seller,
                                    UUID customerPaymentType,
                                    String transactionCode,
                                    String description) {
}
