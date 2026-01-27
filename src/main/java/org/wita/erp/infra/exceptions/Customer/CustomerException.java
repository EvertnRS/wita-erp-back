package org.wita.erp.infra.exceptions.Customer;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class CustomerException extends BaseException {
    public CustomerException(String message, HttpStatus status) {
        super(message, status);
    }
}
