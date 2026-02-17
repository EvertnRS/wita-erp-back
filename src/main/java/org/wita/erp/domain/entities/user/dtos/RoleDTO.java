package org.wita.erp.domain.entities.user.dtos;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public record RoleDTO(
        @Schema(description = "Role's unique identifier", example = "2")
        Long id,
        @Schema(description = "Role's name", example = "ADMIN")
        String role,
        @Schema(description = "List of permissions associated with the role", example = "[\"USER_READ\", \"USER_CREATE\"]")
        Set<String> permissions
) {
}
