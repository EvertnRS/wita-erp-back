package org.wita.erp.domain.entities.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.wita.erp.domain.entities.user.role.Role;

import java.util.UUID;

public record UserDTO(
        @Schema(description = "User's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "User's name", example = "John Doe")
        String name,
        @Schema(description = "User's email", example = "john@example.com")
        String email,
        @Schema(description = "User's role", example = "ADMIN")
        Role role,
        @Schema(description = "Indicates if the user is active", example = "true")
        Boolean active
        ) {
}
