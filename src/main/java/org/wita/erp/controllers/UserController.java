package org.wita.erp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.User.*;
import org.wita.erp.domain.User.Dtos.UpdateUserRequestDTO;
import org.wita.erp.domain.User.Dtos.UserDTO;
import org.wita.erp.domain.User.Mappers.UserMapper;
import org.wita.erp.repositories.RoleRepository;
import org.wita.erp.repositories.UserRepository;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(@PageableDefault(size = 10, sort = "name") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        Page<User> userPage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            userPage = userRepository.findBySearchTerm(searchTerm, pageable);
        } else  {
            userPage = userRepository.findAll(pageable);
        }

        return ResponseEntity.ok(userPage.map(userMapper::toUserDTO));

    }


    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable UUID id, @RequestBody @Valid UpdateUserRequestDTO data) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedInUser == null) return ResponseEntity.status(401).build();

        User user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.status(404).build();

        User userWithEmail = (User) userRepository.findByEmail(data.email());
        if (userWithEmail != null) return ResponseEntity.status(409).build();

        userMapper.updateUserFromDTO(data, user);
        if (data.password() != null && !data.password().isBlank()) {
            String encryptedPass = new BCryptPasswordEncoder().encode(data.password());
            user.setPassword(encryptedPass);
        }
        if (data.role() != null) {
            Role role = roleRepository.findById(data.role()).orElse(null);
            if (role == null) return ResponseEntity.badRequest().build();
            user.setRole(role);
        }

        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toUserDTO(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> delete(@PathVariable UUID id) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedInUser == null) return ResponseEntity.status(401).build();

        User user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.status(404).build();
        user.setActive(false);
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toUserDTO(user));
    }
}
