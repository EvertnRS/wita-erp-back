package org.wita.erp.domain.entities.customer.dtos;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record CreateCustomerRequestDTO(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String address,
        @NotBlank @CPF String cpf,
        @Past @NotNull LocalDate birthDate
) {
}
