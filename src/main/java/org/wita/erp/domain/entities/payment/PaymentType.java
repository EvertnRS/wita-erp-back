package org.wita.erp.domain.entities.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentType {
    @GeneratedValue(strategy = GenerationType.AUTO) @Id
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "is_immediate", nullable = false)
    private Boolean isImmediate;

    @Column(name = "allows_installments", nullable = false)
    private Boolean allowsInstallments;

    @Column(name = "max_installments", nullable = false)
    private Integer maxInstallments;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
