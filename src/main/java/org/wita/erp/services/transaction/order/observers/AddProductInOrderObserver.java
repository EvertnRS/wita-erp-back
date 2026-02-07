package org.wita.erp.services.transaction.order.observers;

import org.wita.erp.domain.entities.transaction.order.dtos.ProductOrderRequestDTO;

import java.util.UUID;

public record AddProductInOrderObserver(UUID order, UUID movementReason, ProductOrderRequestDTO stockDifference) {
}
