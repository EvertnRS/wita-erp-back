package org.wita.erp.domain.entities.payment.company.dtos;

import java.util.UUID;

public record CompanyPaymentTypeDTO(UUID id, String bankCode, String lastFourDigits, String brand, Integer closingDay) {
}
