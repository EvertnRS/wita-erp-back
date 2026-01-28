package org.wita.erp.domain.entities.supplier.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateSupplierRequestDTO(@NotBlank String name, @NotBlank @Email String email, @NotBlank String address, @NotBlank String cnpj) {
}
