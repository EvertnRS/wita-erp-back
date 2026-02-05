package org.wita.erp.domain.entities.payment.company.dtos;

import jakarta.validation.constraints.Positive;
import org.wita.erp.domain.entities.payment.company.CompanyPaymentType;
import org.wita.erp.domain.entities.payment.PaymentMethod;

public record UpdateCompanyPaymentTypeRequestDTO(PaymentMethod paymentMethod,
                                                 Boolean isImmediate,
                                                 Boolean allowsInstallments,
                                                 @Positive Integer maxInstallments,
                                                 String bankCode,
                                                 String agencyNumber,
                                                 String accountNumber,
                                                 String lastFourDigits,
                                                 String brand,
                                                 Integer closingDay) {

}
