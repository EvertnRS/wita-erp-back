package org.wita.erp.domain.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(
        @NotBlank @Email String email,
        @NotBlank String password) {
}
