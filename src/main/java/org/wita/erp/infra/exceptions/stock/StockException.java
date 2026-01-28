package org.wita.erp.infra.exceptions.stock;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class StockException extends BaseException {
    public StockException(String message, HttpStatus status) {
        super(message, status);    }
}
