package org.wita.erp.domain.entities.supplier.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateSupplierRequestDTO(
        @Schema(description = "New name of the supplier", example = "Acme Corporation")
        String name,
        @Schema(description = "New email of the supplier", example = "acme@contact.com")
        @Email String email,
        @Schema(description = "New address of the supplier", example = "123 Main St, Anytown, USA")
        String address,
        @Schema(description = "New document number of the supplier", example = "12.345.678/0001-90")
        String cnpj) {
}
