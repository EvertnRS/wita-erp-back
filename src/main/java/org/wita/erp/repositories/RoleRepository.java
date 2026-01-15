package org.wita.erp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wita.erp.domain.User.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
