package org.wita.erp.services.transaction.order.observers;

import org.wita.erp.domain.entities.transaction.PaymentStatus;

import java.util.UUID;

public record OrderStatusChangedObserver(UUID order, PaymentStatus newStatus) {
}

