package org.wita.erp.domain.repositories.user.authentication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wita.erp.domain.entities.user.authentication.UserAuthentication;

import java.util.Optional;
import java.util.UUID;

public interface UserAuthenticationRepository extends JpaRepository<UserAuthentication, UUID> {
    Optional<UserAuthentication> findByUserId(UUID userId);

}
