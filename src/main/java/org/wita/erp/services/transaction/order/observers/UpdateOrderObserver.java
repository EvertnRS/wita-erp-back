package org.wita.erp.services.transaction.order.observers;

import java.util.UUID;

public record UpdateOrderObserver(UUID order, UUID movementReason) {
}

