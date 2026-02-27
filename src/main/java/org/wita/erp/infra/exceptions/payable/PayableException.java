package org.wita.erp.infra.exceptions.payable;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class PayableException extends BaseException {
    public PayableException(String message, HttpStatus status) {
        super(message, status);
    }
}
