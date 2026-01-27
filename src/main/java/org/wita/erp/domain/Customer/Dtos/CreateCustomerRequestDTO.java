package org.wita.erp.domain.Customer.Dtos;

import jakarta.persistence.Column;
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
