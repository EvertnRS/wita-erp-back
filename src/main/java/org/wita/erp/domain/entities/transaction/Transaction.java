package org.wita.erp.domain.entities.transaction;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.wita.erp.domain.entities.payment.PaymentType;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Transaction {
    @GeneratedValue(strategy = GenerationType.AUTO) @Id
    private UUID id;

    @Positive
    @Column(nullable = false, precision = 15, scale = 2)
    protected BigDecimal value;

    @Column(name = "transaction_code", nullable = false, unique = true)
    private String transactionCode;

    private String description;

    @ManyToOne
    @JoinColumn(name = "payment_type_id", nullable = false)
    private PaymentType paymentType;

    @Column(nullable = false)
    private Boolean active = true;
}
