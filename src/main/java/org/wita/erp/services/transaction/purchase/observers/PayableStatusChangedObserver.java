package org.wita.erp.services.transaction.purchase.observers;

import org.wita.erp.domain.entities.transaction.PaymentStatus;

import java.util.UUID;

public record PayableStatusChangedObserver(UUID purchase, PaymentStatus paymentStatus) {
}

