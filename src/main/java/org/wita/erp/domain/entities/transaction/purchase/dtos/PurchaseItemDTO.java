package org.wita.erp.domain.entities.transaction.purchase.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record PurchaseItemDTO(UUID productId, String ProductName, BigDecimal unitPrice, Integer quantity, BigDecimal total) {
}
