package org.wita.erp.controllers.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.controllers.user.docs.UserDocs;
import org.wita.erp.domain.entities.user.dtos.UpdateUserRequestDTO;
import org.wita.erp.domain.entities.user.dtos.UserDTO;

import org.wita.erp.services.user.UserService;

import java.util.UUID;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController implements UserDocs {
    private final UserService userService;

    @GetMapping(produces = "application/json")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(@PageableDefault(size = 10, sort = "name") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return userService.getAllUsers(pageable, searchTerm);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<UserDTO> update(@PathVariable UUID id, @RequestBody @Valid UpdateUserRequestDTO data) {
        return userService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResponseEntity<UserDTO> delete(@PathVariable UUID id) {
        return userService.delete(id);
    }
}
