package org.wita.erp.services.transaction.purchase.observers;

import java.util.UUID;

public record CreatePurchaseObserver(UUID purchase, UUID movementReason) {
}

