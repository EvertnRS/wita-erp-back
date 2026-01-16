package org.wita.erp.domain.User.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(
        @NotBlank @Email String email,
        @NotBlank String name,
        @NotBlank String password,
        @NotBlank Long role
) {
}
