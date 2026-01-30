package org.wita.erp.domain.entities.payment;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "company_payment_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class CompanyPaymentType extends PaymentType {
    @Column(name = "bank_code", nullable = false)
    private String bankCode;

    @Column(name = "agency_number", nullable = false)
    private String agencyNumber;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "last_four_digits")
    private String lastFourDigits;

    @Column(name = "brand")
    private String brand;

   @Min(1)
   @Max(30)
    @Column(name = "closing_day")
    private Integer closingDay;
}
