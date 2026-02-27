package org.wita.erp.domain.entities.user.authentication.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecoveryRequestDTO(
        @Schema(description = "User's email address used to register in system", example = "john@example.com")
        @NotBlank @Email String email) {
}
