package org.wita.erp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wita.erp.domain.User.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
