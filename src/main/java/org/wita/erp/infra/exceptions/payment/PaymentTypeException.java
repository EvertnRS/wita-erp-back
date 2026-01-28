package org.wita.erp.infra.exceptions.payment;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class PaymentTypeException extends BaseException {
    public PaymentTypeException(String message, HttpStatus status) {
        super(message, status);
    }
}
