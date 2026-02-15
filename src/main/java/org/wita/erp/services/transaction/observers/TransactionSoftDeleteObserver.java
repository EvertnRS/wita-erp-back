package org.wita.erp.services.transaction.observers;

import java.util.UUID;

public record TransactionSoftDeleteObserver(UUID transaction, String reason) {
}

