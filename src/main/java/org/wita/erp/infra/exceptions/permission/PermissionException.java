package org.wita.erp.infra.exceptions.permission;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class PermissionException extends BaseException {
    public PermissionException(String message, HttpStatus status) {
        super(message, status);
    }
}
