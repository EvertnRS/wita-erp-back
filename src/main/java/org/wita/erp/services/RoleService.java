package org.wita.erp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.user.dtos.CreateRoleRequestDTO;
import org.wita.erp.domain.user.dtos.UpdateRoleRequestDTO;
import org.wita.erp.domain.user.Permission;
import org.wita.erp.domain.user.Role;
import org.wita.erp.infra.exceptions.permission.PermissionException;
import org.wita.erp.infra.exceptions.role.RoleException;
import org.wita.erp.repositories.PermissionRepository;
import org.wita.erp.repositories.RoleRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public ResponseEntity<Page<Role>> getAllRoles(Pageable pageable, String searchTerm) {
        Page<Role> rolePage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            rolePage = roleRepository.findByRole(searchTerm, pageable);
        } else {
            rolePage = roleRepository.findAll(pageable);
        }

        return ResponseEntity.ok(rolePage);
    }

    public ResponseEntity<Role> save(CreateRoleRequestDTO data) {
        Set<Permission> permissions = new HashSet<>();
        data.permissions().forEach(permissionId -> {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new PermissionException("Permission " + permissionId + " not found", HttpStatus.NOT_FOUND));
            permissions.add(permission);
        });

        Role role = new Role();
        role.setRole(data.name());
        role.setPermissions(permissions);
        roleRepository.save(role);

        return ResponseEntity.ok(role);
    }

    public ResponseEntity<Role> update(Long id, UpdateRoleRequestDTO data) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleException("Role not found", HttpStatus.NOT_FOUND));

        Set<Permission> permissions = role.getPermissions();

        if (data.permissions() != null) {
            data.permissions().forEach(permissionId -> {
                Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new PermissionException("Permission " + permissionId + " not found", HttpStatus.NOT_FOUND));
                permissions.add(permission);
            });
            role.setPermissions(permissions);
        }

        if (data.name() != null) role.setRole(data.name());

        roleRepository.save(role);

        return ResponseEntity.ok(role);
    }

    public ResponseEntity<Role> delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleException("Role not found", HttpStatus.NOT_FOUND));
        role.setActive(false);
        roleRepository.save(role);
        return ResponseEntity.ok(role);
    }
}
