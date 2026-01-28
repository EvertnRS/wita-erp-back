package org.wita.erp.infra.exceptions.stock;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class MovementReasonException extends BaseException {
    public MovementReasonException(String message, HttpStatus status) {
        super(message, status);    }
}
