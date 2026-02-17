package org.wita.erp.controllers.user.role;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.controllers.user.docs.RoleDocs;
import org.wita.erp.domain.entities.user.dtos.RoleDTO;
import org.wita.erp.domain.entities.user.role.dtos.CreateRoleRequestDTO;
import org.wita.erp.domain.entities.user.role.dtos.DeleteRoleRequestDTO;
import org.wita.erp.domain.entities.user.role.dtos.UpdateRoleRequestDTO;
import org.wita.erp.services.user.role.RoleService;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController implements RoleDocs {
    private final RoleService roleService;

    @GetMapping(produces = "application/json")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<Page<RoleDTO>> getAllRoles(@PageableDefault(size = 10, sort = "role") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return roleService.getAllRoles(pageable, searchTerm);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<RoleDTO> create(@RequestBody CreateRoleRequestDTO data) {
        return roleService.save(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<RoleDTO> update(@PathVariable Long id, @RequestBody UpdateRoleRequestDTO data) {
        return roleService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ResponseEntity<RoleDTO> delete(@PathVariable Long id, @RequestBody DeleteRoleRequestDTO data) {
        return roleService.delete(id, data);
    }

}
