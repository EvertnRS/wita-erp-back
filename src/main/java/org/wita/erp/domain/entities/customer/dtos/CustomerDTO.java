package org.wita.erp.domain.entities.customer.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

public record CustomerDTO(
        @Schema(description = "Customer's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "Customer's name", example = "John Doe")
        String name,
        @Schema(description = "Customer's email", example = "john@example.com")
        String email,
        @Schema(description = "Customer's address", example = "123 Main St, City, Country")
        String address,
        @Schema(description = "Customer's document number (CPF)", example = "123.456.789-00")
        String cpf,
        @Schema(description = "Customer's birth date", example = "1990-01-01")
        LocalDate birthDate,
        @Schema(description = "Indicates if the customer is active", example = "true")
        Boolean active) {
}
