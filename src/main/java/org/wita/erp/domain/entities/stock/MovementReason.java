package org.wita.erp.domain.entities.stock;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.UUID;

@Entity
@Table(name = "movement_reason")
@Getter
@Setter
@Audited
@NoArgsConstructor
@AllArgsConstructor
public class MovementReason {
    @GeneratedValue(strategy = GenerationType.AUTO) @Id
    private UUID id;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private Boolean active = true;
}
