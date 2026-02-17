package org.wita.erp.services.user.role;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.user.dtos.RoleDTO;
import org.wita.erp.domain.entities.user.mappers.RoleMapper;
import org.wita.erp.domain.entities.audit.EntityType;
import org.wita.erp.domain.entities.user.role.Permission;
import org.wita.erp.domain.entities.user.role.Role;
import org.wita.erp.domain.entities.user.role.dtos.CreateRoleRequestDTO;
import org.wita.erp.domain.entities.user.role.dtos.DeleteRoleRequestDTO;
import org.wita.erp.domain.entities.user.role.dtos.UpdateRoleRequestDTO;
import org.wita.erp.domain.repositories.user.role.PermissionRepository;
import org.wita.erp.domain.repositories.user.role.RoleRepository;
import org.wita.erp.infra.exceptions.permission.PermissionException;
import org.wita.erp.infra.exceptions.role.RoleException;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;
import org.wita.erp.services.user.role.observers.RoleSoftDeleteObserver;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;
    private final ApplicationEventPublisher publisher;

    public ResponseEntity<Page<RoleDTO>> getAllRoles(Pageable pageable, String searchTerm) {
        Page<Role> rolePage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            rolePage = roleRepository.findByRole(searchTerm, pageable);
        } else {
            rolePage = roleRepository.findAll(pageable);
        }

        return ResponseEntity.ok(rolePage.map(roleMapper::toDTO));
    }

    public ResponseEntity<RoleDTO> save(CreateRoleRequestDTO data) {
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

        return ResponseEntity.status(HttpStatus.CREATED).body(roleMapper.toDTO(role));
    }

    public ResponseEntity<RoleDTO> update(Long id, UpdateRoleRequestDTO data) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleException("Role not found", HttpStatus.NOT_FOUND));

        Set<Permission> permissions = new HashSet<>();

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

        return ResponseEntity.ok(roleMapper.toDTO(role));
    }

    public ResponseEntity<RoleDTO> delete(Long id, DeleteRoleRequestDTO data) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleException("Role not found", HttpStatus.NOT_FOUND));
        role.setActive(false);
        roleRepository.save(role);

        this.auditRoleSoftDelete(id, data.reason());
        this.roleCascadeDelete(id);

        return ResponseEntity.ok(roleMapper.toDTO(role));
    }

    @Async
    public void auditRoleSoftDelete(Long id, String reason){
        publisher.publishEvent(new SoftDeleteLogObserver(id.toString(), EntityType.ROLE.getEntityType(), reason));
    }

    @Async
    public void roleCascadeDelete(Long id){
        publisher.publishEvent(new RoleSoftDeleteObserver(id));
    }
}
