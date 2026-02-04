package org.wita.erp.domain.entities.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.wita.erp.domain.entities.customer.Customer;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.entities.user.Permission;
import org.wita.erp.domain.entities.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @GeneratedValue(strategy = GenerationType.AUTO) @Id
    private UUID id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal value;

    @DecimalMin("0.00")
    @DecimalMax("1.00")
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal discount;

    @Column(name = "transaction_code", nullable = false, unique = true)
    private String transactionCode;

    private String description;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "payment_type_id", nullable = false)
    private PaymentType paymentType;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
    }

    @Column(nullable = false)
    private Boolean active = true;

    public void applyOrderDiscount() {

        BigDecimal subTotal = items.stream()
                .map(OrderItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (discount.compareTo(BigDecimal.ZERO) <= 0) {
            this.value = subTotal;
            return;
        }

        BigDecimal orderDiscount =
                subTotal.multiply(discount);

        this.value = subTotal.subtract(orderDiscount);
    }
}
