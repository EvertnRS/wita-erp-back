package org.wita.erp.domain.entities.customer.Dtos;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateCustomerRequestDTO(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String address,
        @NotBlank String cpf,
        @Past @NotNull LocalDate birthDate
) {
}
