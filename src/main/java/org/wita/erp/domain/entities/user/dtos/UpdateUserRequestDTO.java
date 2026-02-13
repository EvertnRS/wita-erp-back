package org.wita.erp.domain.entities.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

public record UpdateUserRequestDTO(
        @Schema(description = "User's new name", example = "John Doe")
        String name,
        @Schema(description = "User's new email", example = "john@example.com")
        @Email String email,
        @Schema(description = "User's new password (send only if you want to update the password)", example = "StrongP@ssw0rd!!")
        String password,
        @Schema(description = "User's new role ID", example = "1")
        Long role) {
}
