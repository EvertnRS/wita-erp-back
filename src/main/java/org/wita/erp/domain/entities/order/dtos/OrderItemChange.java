package org.wita.erp.domain.entities.order.dtos;
import java.util.UUID;

public record OrderItemChange(UUID productId, int quantityDifference) {
}