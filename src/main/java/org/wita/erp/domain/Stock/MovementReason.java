package org.wita.erp.domain.Stock;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "movement_reason")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovementReason {
    @GeneratedValue(strategy = GenerationType.AUTO) @Id
    private UUID id;

    @Column(nullable = false)
    private String reason;
}
