package org.wita.erp.domain.entities.transaction.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.wita.erp.domain.entities.customer.Customer;
import org.wita.erp.domain.entities.transaction.Transaction;
import org.wita.erp.domain.entities.user.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Order extends Transaction {

    @DecimalMin("0.00")
    @DecimalMax("1.00")
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal discount;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        this.items.add(item);
        item.setOrders(this);
    }

    public void removeItens() {
        this.items.clear();
    }

    public void applyOrderDiscount() {

        BigDecimal subTotal = items.stream()
                .map(OrderItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (discount == null || discount.compareTo(BigDecimal.ZERO) <= 0) {
            this.value = subTotal;
            return;
        }

        BigDecimal orderDiscount =
                subTotal
                        .multiply(discount)
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

        this.value = subTotal.subtract(orderDiscount);
    }

}
