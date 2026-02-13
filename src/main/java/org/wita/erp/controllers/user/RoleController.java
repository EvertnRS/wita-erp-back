package org.wita.erp.controllers.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.entities.user.dtos.CreateRoleRequestDTO;
import org.wita.erp.domain.entities.user.dtos.UpdateRoleRequestDTO;
import org.wita.erp.domain.entities.user.Role;
import org.wita.erp.services.user.role.RoleService;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<Page<Role>> getAllRoles(@PageableDefault(size = 10, sort = "role") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return roleService.getAllRoles(pageable, searchTerm);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<Role> create(@RequestBody CreateRoleRequestDTO data) {
        return roleService.save(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<Role> update(@PathVariable Long id, @RequestBody UpdateRoleRequestDTO data) {
        return roleService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ResponseEntity<Role> delete(@PathVariable Long id) {
        return roleService.delete(id);
    }

}
