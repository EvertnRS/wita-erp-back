package org.wita.erp.domain.entities.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ConfirmTwoFactorAuthenticationRequestDTO(
        @Schema(description = "2FA code with six digits", example = "123456")
        @NotBlank(message = "Code is required")
        @Pattern(
                regexp = "^\\d{6}$",
                message = "Code must contain exactly 6 digits"
        )
        String code) {
}
