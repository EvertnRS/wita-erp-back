package org.wita.erp.domain.entities.payment.company.dtos;

import org.wita.erp.domain.entities.payment.PaymentMethod;

public record UpdateCompanyPaymentTypeRequestDTO(PaymentMethod paymentMethod,
                                                 Boolean isImmediate,
                                                 Boolean allowsInstallments,
                                                 String bankCode,
                                                 String agencyNumber,
                                                 String accountNumber,
                                                 String lastFourDigits,
                                                 String brand,
                                                 Integer closingDay) {

}
