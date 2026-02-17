package org.wita.erp.domain.repositories.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.wita.erp.domain.entities.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "(LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "LOWER(CAST(u.id AS STRING)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> findBySearchTerm(String searchTerm, Pageable pageable);

    /**
     * Carrega o usuário com as permissions para fazer a verificação de permissões no Spring Security
     */
    @Query("SELECT u FROM User u JOIN FETCH u.role r JOIN FETCH r.permissions WHERE u.email = :email")
    UserDetails findByEmailWithPermissions(String email);

    List<User> findAllByResetTokenIsNotNull();

    @Modifying
    @Query(value = """
    UPDATE users
    SET active = false
    WHERE role_id = :roleId
    RETURNING id
""", nativeQuery = true)
    List<UUID> cascadeDeleteFromRole(Long roleId);

}
