package org.wita.erp.infra.exceptions.product;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class ProductException extends BaseException {
    public ProductException(String message, HttpStatus status) {
        super(message, status);
    }
}
