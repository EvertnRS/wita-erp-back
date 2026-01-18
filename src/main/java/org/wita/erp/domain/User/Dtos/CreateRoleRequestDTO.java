package org.wita.erp.domain.User.Dtos;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record CreateRoleRequestDTO(
        @NotBlank String name,
        @NotBlank Set<Long> permissions
) {}
