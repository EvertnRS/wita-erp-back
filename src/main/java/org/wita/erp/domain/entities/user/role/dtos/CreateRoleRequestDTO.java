package org.wita.erp.domain.entities.user.role.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CreateRoleRequestDTO(
        @Schema(description = "Name of the role", example = "ADMIN")
        @NotBlank String name,
        @Schema(description = "List of permission IDs associated with the role (Replaces the entire list of permissions)", example = "[1, 2, 3]")
        @NotNull Set<Long> permissions
) {}
