package org.wita.erp.domain.entities.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestResetDTO(
        @Schema(description = "New password for the user account", example = "newSecurePassword123!")
        @NotBlank String password) {
}
