package org.wita.erp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.User.*;
import org.wita.erp.domain.User.Dtos.AuthenticationDTO;
import org.wita.erp.domain.User.Dtos.LoginResponseDTO;
import org.wita.erp.domain.User.Dtos.RegisterDTO;
import org.wita.erp.domain.User.Dtos.UserDTO;
import org.wita.erp.infra.security.TokenService;
import org.wita.erp.repositories.RoleRepository;
import org.wita.erp.repositories.UserRepository;

import java.util.Optional;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(userNamePassword);

        var user =  (User) auth.getPrincipal();
        var token = tokenService.generateToken(user);

        return ResponseEntity.ok(new LoginResponseDTO(new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getActive()), token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid RegisterDTO data) {
        if(this.userRepository.findByEmail(data.email()) != null) return ResponseEntity.badRequest().build(); //já existe alguem com esse login

        Optional<Role> role = this.roleRepository.findById(data.role());

        if(role.isEmpty()) return ResponseEntity.badRequest().build();

        String encryptedPass = new BCryptPasswordEncoder().encode(data.password());
        var newUser = new User(data.name(), encryptedPass, data.email(), role.get());

        this.userRepository.save(newUser);

        return ResponseEntity.ok(new UserDTO(newUser.getId(), newUser.getName(), newUser.getEmail(), newUser.getRole(), newUser.getActive()));
    }
}
