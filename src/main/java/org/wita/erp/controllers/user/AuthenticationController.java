package org.wita.erp.controllers.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.entities.user.dtos.AuthenticationDTO;
import org.wita.erp.domain.entities.user.dtos.LoginResponseDTO;
import org.wita.erp.domain.entities.user.dtos.RegisterDTO;
import org.wita.erp.domain.entities.user.dtos.UserDTO;
import org.wita.erp.services.user.AuthenticationService;
import org.wita.erp.services.user.UserService;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        return authenticationService.login(data);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid RegisterDTO data) {
        return userService.save(data);
    }
}
