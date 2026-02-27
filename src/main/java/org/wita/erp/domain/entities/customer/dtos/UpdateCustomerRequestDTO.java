package org.wita.erp.domain.entities.customer.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UpdateCustomerRequestDTO(
        @Schema(description = "New customer's name", example = "John Doe")
        String name,
        @Schema(description = "New customer's email", example = "john@example.com")
        @Email String email,
        @Schema(description = "New customer's address", example = "123 Main St, City, Country")
        String address,
        @Schema(description = "New customer's document number (CPF)", example = "123.456.789-00")
        String cpf,
        @Schema(description = "New customer's birth date", example = "1990-01-01")
        @Past LocalDate birthDate
) {
}
