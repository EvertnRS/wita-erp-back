package org.wita.erp.domain.entities.supplier.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record SupplierDTO(
        @Schema(description = "Supplier's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "Supplier's name", example = "Acme Corporation")
        String name,
        @Schema(description = "Supplier's address", example = "123 Main St, Springfield, USA")
        String address,
        @Schema(description = "Supplier's email", example = "acme@contact.com")
        String email,
        @Schema(description = "Supplier's document number", example = "12.345.678/0001-90")
        String cnpj,
        @Schema(description = "Indicates if the supplier is active", example = "true")
        Boolean active) {
}
