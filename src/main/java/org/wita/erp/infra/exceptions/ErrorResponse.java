package org.wita.erp.infra.exceptions;

public record ErrorResponse(
        int status,
        String message
) {
}
