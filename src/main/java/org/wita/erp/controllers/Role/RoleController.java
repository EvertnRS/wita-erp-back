package org.wita.erp.controllers.Role;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.wita.erp.domain.User.Dtos.CreateRoleRequestDTO;
import org.wita.erp.domain.User.Dtos.UpdateRoleRequestDTO;
import org.wita.erp.domain.User.Permission;
import org.wita.erp.domain.User.Role;
import org.wita.erp.repositories.PermissionRepository;
import org.wita.erp.repositories.RoleRepository;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @GetMapping
    public ResponseEntity<Page<Role>> getAllUsers(@PageableDefault(size = 10, sort = "name") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        Page<Role> rolePage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            rolePage = roleRepository.findByRole(searchTerm, pageable);
        } else {
            rolePage = roleRepository.findAll(pageable);
        }

        return ResponseEntity.ok(rolePage);
    }

    @PostMapping("/create")
    public ResponseEntity<Role> create(@RequestBody CreateRoleRequestDTO data) {
        Set<Permission> permissions = new HashSet<>();
        data.permissions().forEach(permissionId -> {
            Permission permission = permissionRepository.findById(permissionId).orElse(null);
            if (permission == null) throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
            permissions.add(permission);
        });

        Role role = new Role();
        role.setRole(data.name());
        role.setPermissions(permissions);
        roleRepository.save(role);

        return ResponseEntity.ok(role);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> update(@PathVariable Long id, @RequestBody UpdateRoleRequestDTO data) {
        Role role = roleRepository.findById(id).orElse(null);
        if (role == null) throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

        Set<Permission> permissions = role.getPermissions();

        if (data.permissions() != null) {
            data.permissions().forEach(permissionId -> {
                Permission permission = permissionRepository.findById(permissionId).orElse(null);
                if (permission == null) throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
                permissions.add(permission);
            });
            role.setPermissions(permissions);
        }

        if(data.name() != null) role.setRole(data.name());

        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Role> delete(@PathVariable Long id) {
        Role role = roleRepository.findById(id).orElse(null);
        if(role == null) throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        role.setActive(false);
        roleRepository.save(role);
        return ResponseEntity.ok(role);
    }

}
