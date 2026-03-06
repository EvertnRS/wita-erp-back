package org.wita.erp.controllers.user.permission;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wita.erp.controllers.user.docs.PermissionDocs;
import org.wita.erp.domain.entities.user.role.Permission;
import org.wita.erp.services.user.permission.PermissionService;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController implements PermissionDocs {
    private final PermissionService permissionService;

    @GetMapping(produces = "application/json")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<Page<Permission>> getAllPermissions(@PageableDefault(size = 10, sort = "name") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return permissionService.getAllPermissions(pageable, searchTerm);
    }
}
