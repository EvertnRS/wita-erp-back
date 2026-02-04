package org.wita.erp.domain.entities.purchase;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.wita.erp.domain.entities.order.OrderItem;
import org.wita.erp.domain.entities.payment.CompanyPaymentType;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.supplier.Supplier;
import org.wita.erp.domain.entities.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Positive
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal value;

    @Column(name = "transaction_code", nullable = false, unique = true)
    private String transactionCode;

    private String description;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "payment_type_id", nullable = false)
    private PaymentType paymentType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseItem> items = new ArrayList<>();

    public void addItem(PurchaseItem item) {
        this.items.add(item);
        item.setPurchase(this);
    }

    @Column(nullable = false)
    private Boolean active = true;


}
