package org.wita.erp.services.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.user.dtos.RegisterDTO;
import org.wita.erp.domain.entities.user.dtos.UpdateUserRequestDTO;
import org.wita.erp.domain.entities.user.dtos.UserDTO;
import org.wita.erp.domain.entities.user.mappers.UserMapper;
import org.wita.erp.domain.entities.user.Role;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.infra.exceptions.user.UserException;
import org.wita.erp.infra.security.TokenService;
import org.wita.erp.domain.repositories.user.RoleRepository;
import org.wita.erp.domain.repositories.user.UserRepository;

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
        if(this.userRepository.findByEmail(data.email()).isPresent()) {
            throw new UserException("Email already registered", HttpStatus.CONFLICT);
        }

        Role role = this.roleRepository.findById(data.role())
                .orElseThrow(() -> new UserException("Role not registered in the system", HttpStatus.NOT_FOUND));

        String encryptedPass = new BCryptPasswordEncoder().encode(data.password());
        var newUser = new User(data.name(), encryptedPass, data.email(), role);

        this.userRepository.save(newUser);

        return ResponseEntity.ok(userMapper.toUserDTO(newUser));
    }

    public ResponseEntity<UserDTO> update(UUID id, UpdateUserRequestDTO data) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        userRepository.findByEmail(data.email())
                .orElseThrow(() -> new UserException("Email already registered", HttpStatus.CONFLICT));

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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));
        user.setActive(false);
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toUserDTO(user));
    }
}
