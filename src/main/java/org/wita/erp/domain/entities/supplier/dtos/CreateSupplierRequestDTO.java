package org.wita.erp.domain.entities.supplier.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CNPJ;

public record CreateSupplierRequestDTO(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String address,
        @NotBlank @CNPJ(message = "invalid CNPJ") String cnpj) {
}
