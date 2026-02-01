package org.wita.erp.domain.entities.order.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDTO(UUID productId, String ProductName, BigDecimal unitPrice, Integer quantity, BigDecimal total) {
}
