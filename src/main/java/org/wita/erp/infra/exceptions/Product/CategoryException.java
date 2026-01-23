package org.wita.erp.infra.exceptions.Product;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class CategoryException extends BaseException {
    public CategoryException(String message, HttpStatus status) {
        super(message, status);
    }
}
