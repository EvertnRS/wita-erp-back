package org.wita.erp.domain.entities.user.authentication.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public record VerifyEmailResponseDTO(
        @Schema(description = "Message indicating the result of the password recovery process", example = "Password recovery email sent successfully")
        String message) {
}
