package org.wita.erp.infra.exceptions.transaction;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class TransactionException extends BaseException {
    public TransactionException(String message, HttpStatus status) {
        super(message, status);
    }
}
