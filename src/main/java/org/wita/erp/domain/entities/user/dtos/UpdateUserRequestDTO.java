package org.wita.erp.domain.entities.user.dtos;

import jakarta.validation.constraints.Email;

public record UpdateUserRequestDTO(
        String name,
        @Email String email,
        String password,
        Long role) {
}
