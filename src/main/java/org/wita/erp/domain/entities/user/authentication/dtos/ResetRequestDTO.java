package org.wita.erp.domain.entities.user.authentication.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ResetRequestDTO(
        @Schema(description = "New password for the user account", example = "newSecurePassword123!")
        @NotBlank String password) {
}
