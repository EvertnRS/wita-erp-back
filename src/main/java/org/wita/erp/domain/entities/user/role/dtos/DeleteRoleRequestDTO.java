package org.wita.erp.domain.entities.user.role.dtos;

import jakarta.validation.constraints.NotBlank;

public record DeleteRoleRequestDTO(
        @NotBlank String reason
) {}
