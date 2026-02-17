package org.wita.erp.domain.entities.customer.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record CreateCustomerRequestDTO(
        @Schema(description = "Customer's name", example = "John Doe")
        @NotBlank String name,
        @Schema(description = "Customer's email", example = "john@example.com")
        @NotBlank @Email String email,
        @Schema(description = "Customer's address", example = "123 Main St, City, Country")
        @NotBlank String address,
        @Schema(description = "Customer's document number (CPF)", example = "123.456.789-00")
        @NotBlank @CPF String cpf,
        @Schema(description = "Customer's birth date", example = "1990-01-01")
        @Past @NotNull LocalDate birthDate
) {
}
