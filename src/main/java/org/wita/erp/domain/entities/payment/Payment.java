package org.wita.erp.domain.entities.payment;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.wita.erp.domain.entities.transaction.PaymentStatus;
import org.wita.erp.domain.entities.transaction.order.Receivable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receivable_id", nullable = false)
    private Receivable receivable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentGateway gateway;

    @Column(name = "gateway_payment_id", nullable = true, length = 100, unique = true)
    private String gatewayPaymentId;

    @Column(name = "gateway_session_id", length = 100)
    private String gatewaySessionId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    @Column(nullable = false)
    private Integer attempts = 0;

    @Column(name = "last_event_at")
    private OffsetDateTime lastEventAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    @PastOrPresent
    private LocalDateTime paidAt;
}
