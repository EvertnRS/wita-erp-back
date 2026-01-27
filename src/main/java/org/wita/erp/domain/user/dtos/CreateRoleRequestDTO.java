package org.wita.erp.domain.user.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CreateRoleRequestDTO(
        @NotBlank String name,
        @NotNull Set<Long> permissions
) {}
