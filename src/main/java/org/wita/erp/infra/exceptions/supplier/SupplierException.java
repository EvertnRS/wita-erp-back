package org.wita.erp.infra.exceptions.supplier;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class SupplierException extends BaseException {
    public SupplierException(String message, HttpStatus status) {
        super(message, status);
    }
}
