package org.wita.erp.infra.exceptions.report;

import org.springframework.http.HttpStatus;
import org.wita.erp.infra.exceptions.BaseException;

public class ReportException extends BaseException {
    public ReportException(String message, HttpStatus status) {
        super(message, status);    }
}
