package org.wita.erp.domain.entities.transaction;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PAID("paid"),
    PENDING("pending"),
    OVERDUE("overdue"),
    CANCELED("canceled"),
    REFUNDED("refunded");

    private final String paymentStatus;


    PaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public boolean isReversal() {
        return this == CANCELED || this == REFUNDED;
    }

    public boolean allowsManualUpdate() {
        return this == PENDING;
    }

}
