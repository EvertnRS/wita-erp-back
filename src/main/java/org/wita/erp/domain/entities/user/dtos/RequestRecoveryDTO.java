package org.wita.erp.domain.entities.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestRecoveryDTO(
        @NotBlank @Email String email) {
}
