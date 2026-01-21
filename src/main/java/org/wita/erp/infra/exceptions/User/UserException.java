package org.wita.erp.infra.exceptions.User;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class UserException extends BaseException {
    public UserException(String message, HttpStatus status) {
        super(message, status);
    }
}
