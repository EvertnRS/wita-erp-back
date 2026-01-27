package org.wita.erp.domain.Customer.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UpdateCustomerRequestDTO(
        String name,
        @Email String email,
        String address,
        String cpf,
        @Past LocalDate birthDate
) {
}
