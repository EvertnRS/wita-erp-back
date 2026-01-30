package org.wita.erp.domain.entities.supplier.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateSupplierRequestDTO(String name, @Email String email, String address, String cnpj) {
}
