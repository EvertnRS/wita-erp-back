package org.wita.erp.domain.repositories.user.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wita.erp.domain.entities.user.role.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
