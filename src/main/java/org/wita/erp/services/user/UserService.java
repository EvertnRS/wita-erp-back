package org.wita.erp.services.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wita.erp.domain.entities.audit.EntityType;
import org.wita.erp.domain.entities.user.role.Role;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.entities.user.dtos.DeleteUserRequestDTO;
import org.wita.erp.domain.entities.user.dtos.RegisterDTO;
import org.wita.erp.domain.entities.user.dtos.UpdateUserRequestDTO;
import org.wita.erp.domain.entities.user.dtos.UserDTO;
import org.wita.erp.domain.entities.user.mappers.UserMapper;
import org.wita.erp.domain.repositories.user.role.RoleRepository;
import org.wita.erp.domain.repositories.user.UserRepository;
import org.wita.erp.infra.exceptions.user.UserException;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;
import org.wita.erp.services.user.authentication.observers.RequestRecoveryObserver;
import org.wita.erp.services.user.authentication.observers.ResetPasswordObserver;
import org.wita.erp.services.user.role.observers.RoleSoftDeleteObserver;
import org.wita.erp.services.user.observers.UserSoftDeleteObserver;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
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

        String encryptedPass = passwordEncoder.encode(data.password());
        var newUser = new User(data.name(), encryptedPass, data.email(), role);

        this.userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toUserDTO(newUser));
    }

    public ResponseEntity<UserDTO> update(UUID id, UpdateUserRequestDTO data) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        Optional<User> existingUser = userRepository.findByEmail(data.email());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
            throw new UserException("Email already registered", HttpStatus.CONFLICT);
        }

        userMapper.updateUserFromDTO(data, user);
        if (data.password() != null && !data.password().isBlank()) {
            String encryptedPass = passwordEncoder.encode(data.password());
            user.setPassword(encryptedPass);
        }
        if (data.role() != null) {
            Role role = roleRepository.findById(data.role())
                    .orElseThrow(() -> new UserException("Role not registered in the system", HttpStatus.NOT_FOUND));
            user.setRole(role);
        }

        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toUserDTO(user));
    }

    public ResponseEntity<UserDTO> delete(UUID id, DeleteUserRequestDTO data) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));
        user.setActive(false);
        userRepository.save(user);

        this.auditUserSoftDelete(id, data.reason());
        this.userCascadeDelete(id);

        return ResponseEntity.ok(userMapper.toUserDTO(user));
    }

    @EventListener
    @Transactional
    public void onRequestRecovery(RequestRecoveryObserver event) {
        event.user().setResetToken(event.encodedToken());
        event.user().setResetTokenExpiresAt(event.expiresAt());
        userRepository.save(event.user());
    }

    @EventListener
    @Transactional
    public void onResetPassword(ResetPasswordObserver event) {
        event.user().setPassword(passwordEncoder.encode(event.newPassword()));
        event.user().setResetToken(null);
        event.user().setResetTokenExpiresAt(null);
        userRepository.save(event.user());
    }

    @EventListener
    public void onRoleSoftDelete(RoleSoftDeleteObserver event) {
        List<UUID> userIds = userRepository.cascadeDeleteFromRole(event.role());
        if(!userIds.isEmpty()){
            for (UUID userId : userIds) {
                this.auditUserSoftDelete(userId, "Cascade delete from role " + event.role());
                this.userCascadeDelete(userId);
            }
        }
    }

    @Async
    public void auditUserSoftDelete(UUID id, String reason){
        publisher.publishEvent(new SoftDeleteLogObserver(id.toString(), EntityType.USER.getEntityType(), reason));
    }

    @Async
    public void userCascadeDelete(UUID id){
        publisher.publishEvent(new UserSoftDeleteObserver(id));
    }
}
