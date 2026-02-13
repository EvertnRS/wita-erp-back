package org.wita.erp.domain.entities.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegisterDTO(
        @Schema(description = "User's email address used to register in system", example = "john@example.com")
        @NotBlank @Email String email,
        @Schema(description = "User's name", example = "John Doe")
        @NotBlank String name,
        @Schema(description = "User's password", example = "StrongP@ssw0rd!")
        @NotBlank String password,
        @Schema(description = "ID of the role assigned to the user", example = "2")
        @NotNull @Positive Long role
) {
}
