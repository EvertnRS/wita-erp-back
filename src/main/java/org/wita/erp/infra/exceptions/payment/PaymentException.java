package org.wita.erp.infra.exceptions.payment;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class PaymentException extends BaseException {
    public PaymentException(String message, HttpStatus status) {
        super(message, status);
    }
}
