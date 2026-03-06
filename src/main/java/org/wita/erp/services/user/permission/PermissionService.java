package org.wita.erp.services.user.permission;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.user.role.Permission;
import org.wita.erp.domain.repositories.user.role.PermissionRepository;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public ResponseEntity<Page<Permission>> getAllPermissions(Pageable pageable, String searchTerm) {
        Page<Permission> permissionPage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            permissionPage = permissionRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            permissionPage = permissionRepository.findAll(pageable);
        }

        return ResponseEntity.ok(permissionPage);
    }
}
