package org.wita.erp.domain.entities.purchase;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.wita.erp.domain.entities.payment.CompanyPaymentType;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.supplier.Supplier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "purchase")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {
    @GeneratedValue(strategy = GenerationType.AUTO) @Id
    private UUID id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal value;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "payment_type_id", nullable = false)
    private PaymentType paymentType;

    @Column(name = "transaction_code", nullable = false, unique = true)
    private String transactionCode;

    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
