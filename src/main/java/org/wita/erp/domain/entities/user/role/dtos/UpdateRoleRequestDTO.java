package org.wita.erp.domain.entities.user.role.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public record UpdateRoleRequestDTO(
        @Schema(description = "New role's name", example = "ADMIN")
        String name,
        @Schema(description = "List of permission IDs associated with the role", example = "[1, 2, 3]")
        Set<Long> permissions
) {}
