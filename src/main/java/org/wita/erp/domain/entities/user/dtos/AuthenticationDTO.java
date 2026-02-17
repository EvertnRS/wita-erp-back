package org.wita.erp.domain.entities.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(
        @Schema(description = "User's email address", example = "john@example.com")
        @NotBlank @Email String email,
        @Schema(description = "User's password", example = "StrongP@ssw0rd")
        @NotBlank String password) {
}
