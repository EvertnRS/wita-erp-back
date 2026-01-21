package org.wita.erp.infra.exceptions.Auth;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class AuthException extends BaseException {
    public AuthException(String message, HttpStatus status) {
        super(message, status);    }
}
