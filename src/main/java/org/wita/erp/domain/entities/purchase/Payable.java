package org.wita.erp.domain.entities.purchase;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.wita.erp.domain.entities.status.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payable {
    @GeneratedValue(strategy = GenerationType.AUTO) @Id
    private UUID id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal value;

    @Future
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
