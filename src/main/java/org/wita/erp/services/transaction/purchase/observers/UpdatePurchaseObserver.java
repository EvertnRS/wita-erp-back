package org.wita.erp.services.transaction.purchase.observers;

import java.util.UUID;

public record UpdatePurchaseObserver(UUID purchase, UUID movementReason) {
}

