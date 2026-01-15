package org.wita.erp.domain.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Table(name = "role")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column (unique = true, nullable = false)
    private String role;

    @PrePersist
    @PreUpdate
    public void sanitize() {
        if (this.role != null) {
            this.role = this.role.trim().toUpperCase();
        }
    }
}
