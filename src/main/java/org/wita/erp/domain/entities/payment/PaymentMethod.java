package org.wita.erp.domain.entities.payment;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CREDIT_CARD("credit card"),
    DEBIT_CARD("debit card"),
    BANK_TRANSFER("bank transfer"),
    CASH("cash"),
    PIX("pix"),
    OTHER("other");

    private final String paymentMethod;

    PaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

}
