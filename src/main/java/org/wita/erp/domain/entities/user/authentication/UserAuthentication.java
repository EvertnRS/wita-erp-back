package org.wita.erp.domain.entities.user.authentication;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;
import org.wita.erp.domain.entities.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Audited
@Table(name = "users_authentication")
public class UserAuthentication {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "secret")
    private String secret;

    @Column(name = "temporary_secret")
    private String temporarySecret;

    @Column(name = "temporary_secret_expiration")
    private LocalDateTime temporarySecretExpiration;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

