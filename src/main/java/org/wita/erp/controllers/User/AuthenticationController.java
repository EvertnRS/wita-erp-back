package org.wita.erp.controllers.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.User.Dtos.AuthenticationDTO;
import org.wita.erp.domain.User.Dtos.LoginResponseDTO;
import org.wita.erp.domain.User.Dtos.RegisterDTO;
import org.wita.erp.domain.User.Dtos.UserDTO;
import org.wita.erp.services.AuthenticationService;
import org.wita.erp.services.UserService;

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
