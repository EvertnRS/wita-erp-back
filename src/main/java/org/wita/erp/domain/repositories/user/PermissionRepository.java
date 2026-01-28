package org.wita.erp.domain.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wita.erp.domain.entities.user.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
