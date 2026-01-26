package org.wita.erp.domain.user.dtos;

import jakarta.validation.constraints.Email;

public record UpdateUserRequestDTO(
        String name,
        @Email String email,
        String password,
        Long role) {
}
