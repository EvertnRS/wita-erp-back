package org.wita.erp.domain.entities.stock;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.entities.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_movement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {
    @GeneratedValue(strategy = GenerationType.AUTO) @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "movement_type", nullable = false)
    private StockMovementType stockMovementType;

    @Column(nullable = false)
    private Integer quantity;

    @OneToOne
    @JoinColumn(name = "movement_reason_id", nullable = false)
    private MovementReason movementReason;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean active = true;

    // TODO: relacionar com compra
}
