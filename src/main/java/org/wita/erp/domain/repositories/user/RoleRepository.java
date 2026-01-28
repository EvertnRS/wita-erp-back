package org.wita.erp.domain.repositories.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wita.erp.domain.entities.user.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Page<Role> findByRole(String role, Pageable pageable);
}
