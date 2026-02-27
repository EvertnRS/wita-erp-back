package org.wita.erp.infra.exceptions.purchase;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class PurchaseException extends BaseException {
    public PurchaseException(String message, HttpStatus status) {
        super(message, status);
    }
}
