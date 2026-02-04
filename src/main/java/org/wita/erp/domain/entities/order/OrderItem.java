package org.wita.erp.domain.entities.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.infra.exceptions.order.OrderException;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Positive
    @Column(nullable = false)
    private Integer quantity;

    @Positive
    @Column(name = "unit_price",nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Positive
    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    public void calculateTotal(BigDecimal itemDiscount) {

        BigDecimal gross = unitPrice
                .multiply(BigDecimal.valueOf(quantity));

        BigDecimal safeDiscount =
                itemDiscount != null ? itemDiscount : BigDecimal.ZERO;

        if (safeDiscount.compareTo(gross) > 0) {
            throw new OrderException("Item discount exceeds gross value", HttpStatus.BAD_REQUEST);
        }

        this.total = gross.subtract(safeDiscount);
    }

}