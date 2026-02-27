package org.wita.erp.domain.entities.supplier.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CNPJ;

public record CreateSupplierRequestDTO(
        @Schema(description = "Supplier's name", example = "Acme Corporation")
        @NotBlank String name,
        @Schema(description = "Supplier's email", example = "acme@contact.com")
        @NotBlank @Email String email,
        @Schema(description = "Supplier's address", example = "123 Main St, City, Country")
        @NotBlank String address,
        @Schema(description = "Supplier's document number (CNPJ)", example = "12.345.678/0001-90")
        @NotBlank @CNPJ(message = "invalid CNPJ") String cnpj) {
}
