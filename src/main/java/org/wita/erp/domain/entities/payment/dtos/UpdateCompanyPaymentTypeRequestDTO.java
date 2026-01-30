package org.wita.erp.domain.entities.payment.dtos;

import jakarta.validation.constraints.Positive;
import org.wita.erp.domain.entities.payment.CompanyPaymentType;
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

    public void applyTo(CompanyPaymentType entity) {
        if (bankCode != null) entity.setBankCode(bankCode);
        if (agencyNumber != null) entity.setAgencyNumber(agencyNumber);
        if (accountNumber != null) entity.setAccountNumber(accountNumber);
        if (lastFourDigits != null) entity.setLastFourDigits(lastFourDigits);
        if (brand != null) entity.setBrand(brand);
        if (closingDay != null) entity.setClosingDay(closingDay);
    }

}
