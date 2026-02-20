package org.wita.erp.domain.entities.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public record TwoFactorAuthenticationRequestDTO(
        @Schema(description = "Message indicating the result of the 2FA process", example = "Email 2FA request has been sent successfully.")
        String message) {
}
