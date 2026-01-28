package org.wita.erp.domain.entities.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "permission")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column (unique = true, nullable = false)
    private String name;

    @PrePersist
    @PreUpdate
    public void sanitize() {
        if (this.name != null) {
            this.name = this.name.trim().toUpperCase();
        }
    }
}
