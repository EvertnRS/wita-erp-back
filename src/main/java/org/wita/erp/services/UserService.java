package org.wita.erp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.User.Dtos.*;
import org.wita.erp.domain.User.Mappers.UserMapper;
import org.wita.erp.domain.User.Role;
import org.wita.erp.domain.User.User;
import org.wita.erp.infra.security.TokenService;
import org.wita.erp.repositories.RoleRepository;
import org.wita.erp.repositories.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable, String searchTerm) {
        Page<User> userPage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            userPage = userRepository.findBySearchTerm(searchTerm, pageable);
        } else  {
            userPage = userRepository.findAll(pageable);
        }

        return ResponseEntity.ok(userPage.map(userMapper::toUserDTO));
    }

    public ResponseEntity<UserDTO> save(RegisterDTO data) {
        if(this.userRepository.findByEmail(data.email()).isPresent()) return ResponseEntity.badRequest().build(); //já existe alguem com esse login

        Optional<Role> role = this.roleRepository.findById(data.role());

        if(role.isEmpty()) return ResponseEntity.badRequest().build();

        String encryptedPass = new BCryptPasswordEncoder().encode(data.password());
        var newUser = new User(data.name(), encryptedPass, data.email(), role.get());

        this.userRepository.save(newUser);

        return ResponseEntity.ok(userMapper.toUserDTO(newUser));
    }

    public ResponseEntity<UserDTO> update(UUID id, UpdateUserRequestDTO data) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.status(404).build();

        Optional<User> userWithEmail = userRepository.findByEmail(data.email());
        if (userWithEmail.isPresent()) return ResponseEntity.status(409).build();

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

    public ResponseEntity<UserDTO> delete(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.status(404).build();
        user.setActive(false);
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toUserDTO(user));
    }
}
