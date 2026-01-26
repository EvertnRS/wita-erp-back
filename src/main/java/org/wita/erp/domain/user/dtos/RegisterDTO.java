package org.wita.erp.domain.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegisterDTO(
        @NotBlank @Email String email,
        @NotBlank String name,
        @NotBlank String password,
        @NotNull @Positive Long role
) {
}
