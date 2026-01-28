package org.wita.erp.domain.entities.customer.dtos;

import jakarta.validation.constraints.Email;
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
