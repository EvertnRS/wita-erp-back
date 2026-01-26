package org.wita.erp.domain.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
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

    // TODO: relacionar com compra
}
