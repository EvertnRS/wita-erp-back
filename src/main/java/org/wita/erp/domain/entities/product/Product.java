package org.wita.erp.domain.entities.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @GeneratedValue(strategy = GenerationType.AUTO) @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Positive
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @DecimalMin("0.00")
    @DecimalMax("1.00")
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal discount;

    @Min(0)
    @Column(name = "min_quantity_for_discount", nullable = false)
    private int minQuantityForDiscount;

    @Min(0)
    @Column(name = "min_quantity", nullable = false)
    private Integer minQuantity;

    @Min(0)
    @Column(name = "quantity_in_stock", nullable = false)
    private Integer quantityInStock;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public BigDecimal calculateItemDiscount(
            BigDecimal unitPrice,
            int quantity
    ) {

        if (discount == null || discount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (quantity < minQuantityForDiscount) {
            return BigDecimal.ZERO;
        }

        BigDecimal gross =
                unitPrice.multiply(BigDecimal.valueOf(quantity));

        return gross
                .multiply(discount)
                .divide(
                        BigDecimal.valueOf(100),
                        2,
                        RoundingMode.HALF_UP
                );
    }


}
