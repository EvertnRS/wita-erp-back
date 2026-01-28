package org.wita.erp.infra.exceptions.role;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class RoleException extends BaseException {
    public RoleException(String message, HttpStatus status) {
        super(message, status);
    }
}
