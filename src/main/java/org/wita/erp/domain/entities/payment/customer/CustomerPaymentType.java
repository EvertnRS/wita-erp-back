package org.wita.erp.domain.entities.payment.customer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.wita.erp.domain.entities.payment.PaymentType;

@Entity
@Table(name = "customer_payment_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class CustomerPaymentType extends PaymentType {
    @Column(name = "supports_refunds", nullable = false)
    private Boolean supportsRefunds;
}
