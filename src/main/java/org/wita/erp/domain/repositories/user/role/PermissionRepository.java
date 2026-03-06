package org.wita.erp.domain.repositories.user.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.user.role.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    @Query("SELECT p FROM Permission p WHERE " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "(LOWER(CAST(p.id AS STRING)) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Permission> findBySearchTerm(String searchTerm, Pageable pageable);
}
