package org.wita.erp.infra.exceptions.order;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class OrderException extends BaseException {
    public OrderException(String message, HttpStatus status) {
        super(message, status);
    }
}
