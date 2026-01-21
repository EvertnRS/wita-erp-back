package org.wita.erp.infra.exceptions.Permission;

import org.springframework.http.HttpStatus;
import org.wita.erp.domain.User.Permission;
import org.wita.erp.infra.exceptions.BaseException;

public class PermissionException extends BaseException {
    public PermissionException(String message, HttpStatus status) {
        super(message, status);
    }
}
