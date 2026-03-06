package org.wita.erp.domain.repositories.user.role;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.entities.user.role.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT r FROM Role r WHERE " +
            "(:searchTerm IS NULL OR :searchTerm = '' OR " +
            "  (LOWER(r.role) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "   LOWER(CAST(r.id AS STRING)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            ") " +
            "AND (:active IS NULL OR r.active = :active)")
    Page<Role> findRoles(Pageable pageable, @Param("searchTerm") String searchTerm, @Param("active") Boolean active);
}
