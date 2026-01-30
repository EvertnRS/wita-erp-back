package org.wita.erp.domain.entities.status;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PAY("pay"),
    PENDING("pending"),
    OVERDUE("overdue"),
    CANCELED("canceled"),
    REFUNDED("refunded");

    private final String paymentStatus;

    PaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

}
