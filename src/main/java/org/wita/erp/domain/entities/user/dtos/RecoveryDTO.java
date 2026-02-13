package org.wita.erp.domain.entities.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public record RecoveryDTO(
        @Schema(description = "Message indicating the result of the password recovery process", example = "Password recovery email sent successfully")
        String message) {
}
