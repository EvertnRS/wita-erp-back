package org.wita.erp.domain.entities.payment.dtos;


import jakarta.validation.constraints.Positive;
import org.wita.erp.domain.entities.payment.CustomerPaymentType;
import org.wita.erp.domain.entities.payment.PaymentMethod;

public record UpdateCustomerPaymentTypeRequestDTO(PaymentMethod paymentMethod,
                                                  Boolean isImmediate,
                                                  Boolean allowsInstallments,
                                                  @Positive Integer maxInstallments,
                                                  Boolean supportsRefunds) {

    public void applyTo(CustomerPaymentType entity) {
        if (supportsRefunds != null) entity.setSupportsRefunds(supportsRefunds);
    }
}
