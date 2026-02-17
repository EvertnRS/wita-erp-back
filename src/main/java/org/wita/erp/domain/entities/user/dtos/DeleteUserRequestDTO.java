package org.wita.erp.domain.entities.user.dtos;

import jakarta.validation.constraints.NotBlank;

public record DeleteUserRequestDTO(
        @NotBlank String reason
) {}
