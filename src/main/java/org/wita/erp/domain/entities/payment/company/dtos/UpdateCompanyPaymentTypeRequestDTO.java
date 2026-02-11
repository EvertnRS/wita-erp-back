package org.wita.erp.domain.entities.payment.company.dtos;

public record UpdateCompanyPaymentTypeRequestDTO(
                                                 Boolean isImmediate,
                                                 Boolean allowsInstallments,
                                                 String bankCode,
                                                 String agencyNumber,
                                                 String accountNumber,
                                                 String lastFourDigits,
                                                 String brand,
                                                 Integer closingDay) {

}
