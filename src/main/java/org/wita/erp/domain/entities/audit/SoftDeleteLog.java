package org.wita.erp.domain.entities.audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "soft_delete_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoftDeleteLog {
    @GeneratedValue(strategy = GenerationType.AUTO) @Id
    private UUID id;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name= "entity_type", nullable = false)
    private String entityType;

    @Column(nullable = false)
    private String reason;

    @CreationTimestamp
    @Column(name = "deleted_at", nullable = false, updatable = false)
    private LocalDateTime deletedAt;
}
