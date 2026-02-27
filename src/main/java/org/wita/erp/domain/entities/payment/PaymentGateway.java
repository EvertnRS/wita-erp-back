package org.wita.erp.domain.entities.payment;

import lombok.Getter;

@Getter
public enum PaymentGateway {
    STRIPE("stripe");

    private final String pg;

    PaymentGateway(String paymentGateway) {
        this.pg = paymentGateway;
    }
}
