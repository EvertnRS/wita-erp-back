package org.wita.erp.domain.entities.status;

public enum PaymentStatus {
    PAY("pay"),
    PENDING("pending");

    private String paymentStatus;

    PaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStatus() {
        return this.paymentStatus;
    }
}
