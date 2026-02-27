package org.wita.erp.services.transaction.order.observers;

import java.util.UUID;

public record CreateOrderObserver(UUID order, UUID movementReason) {
}

