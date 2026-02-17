package org.wita.erp.domain.repositories.user.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wita.erp.domain.entities.user.role.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Page<Role> findByRole(String role, Pageable pageable);
}
