package org.wita.erp.infra.exceptions.receivable;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class ReceivableException extends BaseException {
    public ReceivableException(String message, HttpStatus status) {
        super(message, status);
    }
}
