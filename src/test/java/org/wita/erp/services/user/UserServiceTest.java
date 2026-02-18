package org.wita.erp.services.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.entities.user.dtos.DeleteUserRequestDTO;
import org.wita.erp.domain.entities.user.dtos.RegisterDTO;
import org.wita.erp.domain.entities.user.dtos.UserDTO;
import org.wita.erp.domain.entities.user.mappers.UserMapper;
import org.wita.erp.domain.entities.user.role.Role;
import org.wita.erp.domain.repositories.user.UserRepository;
import org.wita.erp.domain.repositories.user.role.RoleRepository;
import org.wita.erp.infra.exceptions.user.UserException;

import org.wita.erp.domain.entities.user.dtos.UpdateUserRequestDTO;
import org.wita.erp.services.user.authentication.observers.RequestRecoveryObserver;
import org.wita.erp.services.user.authentication.observers.ResetPasswordObserver;
import org.wita.erp.services.user.role.observers.RoleSoftDeleteObserver;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private Role fakeRole;
    private User fakeUser;
    private UserDTO baseUserDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        fakeRole = new Role(2L, "USER", true, null);

        fakeUser = new User("John", "pass", "john@example.com", fakeRole);
        fakeUser.setId(userId);
        fakeUser.setActive(true);

        baseUserDTO = new UserDTO(userId, "John", "john@example.com", fakeRole, true);
    }

    @Test
    @DisplayName("Deve retornar todos os usuários quando o searchTerm for nulo")
    void shouldReturnAllUsersWhenSearchTermIsNull() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> fakePage = new PageImpl<>(List.of(fakeUser));

        Mockito.when(userRepository.findAll(pageable)).thenReturn(fakePage);
        Mockito.when(userMapper.toUserDTO(fakeUser)).thenReturn(baseUserDTO);

        ResponseEntity<Page<UserDTO>> response = userService.getAllUsers(pageable, null);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().getTotalElements());
        Mockito.verify(userRepository).findAll(pageable);
        Mockito.verify(userRepository, Mockito.never()).findBySearchTerm(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("Deve retornar usuários filtrados pelo searchTerm")
    void shouldReturnUsersFilteredBySearchTerm() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> fakePage = new PageImpl<>(List.of(fakeUser));

        Mockito.when(userRepository.findBySearchTerm("john", pageable)).thenReturn(fakePage);
        Mockito.when(userMapper.toUserDTO(fakeUser)).thenReturn(baseUserDTO);

        ResponseEntity<Page<UserDTO>> response = userService.getAllUsers(pageable, "john");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().getTotalElements());
        Mockito.verify(userRepository).findBySearchTerm("john", pageable);
        Mockito.verify(userRepository, Mockito.never()).findAll(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Deve cadastrar um novo usuário com sucesso")
    void shouldRegisterNewUserSuccessfully() {
        RegisterDTO fakeRegisterDTO = new RegisterDTO("john@example.com", "John", "password", 2L);
        UserDTO expectedUserDTO = new UserDTO(null, "John", "john@example.com", fakeRole, true);

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(roleRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(fakeRole));
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("password");
        Mockito.when(userMapper.toUserDTO(Mockito.any())).thenReturn(expectedUserDTO);

        ResponseEntity<UserDTO> response = userService.save(fakeRegisterDTO);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(expectedUserDTO, response.getBody());
        Mockito.verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("Deve lançar UserException ao tentar cadastrar um usuário com email já registrado")
    void shouldThrowUserExceptionWhenEmailAlreadyRegistered() {
        RegisterDTO fakeRegisterDTO = new RegisterDTO("john@example.com", "John", "password", 2L);
        User existingUser = new User("John", "pass", "john@example.com", null);

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existingUser));
        UserException exception = Assertions.assertThrows(UserException.class, () -> userService.save(fakeRegisterDTO));

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("Deve lançar UserException ao tentar cadastrar um usuário com role inexistente")
    void shouldThrowUserExceptionWhenRoleNotFound() {
        RegisterDTO fakeRegisterDTO = new RegisterDTO("john@example.com", "John", "password", 2L);

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(roleRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        UserException exception = Assertions.assertThrows(UserException.class, () -> userService.save(fakeRegisterDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("Deve atualizar um usuário com sucesso")
    void shouldUpdateUserSuccessfully() {
        UpdateUserRequestDTO fakeUpdateDTO = new UpdateUserRequestDTO("John Updated", "john.updated@example.com", null, null);
        UserDTO expectedUserDTO = new UserDTO(userId, "John Updated", "john.updated@example.com", fakeRole, true);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));
        Mockito.when(userRepository.findByEmail(fakeUpdateDTO.email())).thenReturn(Optional.empty());
        Mockito.when(userMapper.toUserDTO(fakeUser)).thenReturn(expectedUserDTO);

        ResponseEntity<UserDTO> response = userService.update(userId, fakeUpdateDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(expectedUserDTO, response.getBody());
        Mockito.verify(userRepository).save(fakeUser);
    }

    @Test
    @DisplayName("Deve atualizar usuário com nova senha com sucesso")
    void shouldUpdateUserWithPasswordSuccessfully() {
        UpdateUserRequestDTO fakeUpdateDTO = new UpdateUserRequestDTO("John Updated", "john.updated@example.com", "newPassword", null);
        UserDTO expectedUserDTO = new UserDTO(userId, "John Updated", "john.updated@example.com", fakeRole, true);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));
        Mockito.when(userRepository.findByEmail(fakeUpdateDTO.email())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        Mockito.when(userMapper.toUserDTO(fakeUser)).thenReturn(expectedUserDTO);

        ResponseEntity<UserDTO> response = userService.update(userId, fakeUpdateDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(expectedUserDTO, response.getBody());
        Mockito.verify(passwordEncoder).encode("newPassword");
        Mockito.verify(userRepository).save(fakeUser);
    }

    @Test
    @DisplayName("Deve atualizar usuário com novo role com sucesso")
    void shouldUpdateUserWithRoleSuccessfully() {
        UpdateUserRequestDTO fakeUpdateDTO = new UpdateUserRequestDTO("John Updated", "john.updated@example.com", null, 3L);
        Role newRole = new Role(3L, "ADMIN", true, null);
        UserDTO expectedUserDTO = new UserDTO(userId, "John Updated", "john.updated@example.com", newRole, true);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));
        Mockito.when(userRepository.findByEmail(fakeUpdateDTO.email())).thenReturn(Optional.empty());
        Mockito.when(roleRepository.findById(3L)).thenReturn(Optional.of(newRole));
        Mockito.when(userMapper.toUserDTO(fakeUser)).thenReturn(expectedUserDTO);

        ResponseEntity<UserDTO> response = userService.update(userId, fakeUpdateDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(expectedUserDTO, response.getBody());
        Mockito.verify(roleRepository).findById(3L);
        Mockito.verify(userRepository).save(fakeUser);
    }

    @Test
    @DisplayName("Deve lançar UserException ao tentar atualizar usuário inexistente")
    void shouldThrowUserExceptionWhenUpdatingNonExistentUser() {
        UpdateUserRequestDTO fakeUpdateDTO = new UpdateUserRequestDTO("John Updated", "john.updated@example.com", null, null);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserException exception = Assertions.assertThrows(UserException.class, () -> userService.update(userId, fakeUpdateDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("Deve lançar UserException ao tentar atualizar email para um já registrado")
    void shouldThrowUserExceptionWhenUpdatingWithExistingEmail() {
        UpdateUserRequestDTO fakeUpdateDTO = new UpdateUserRequestDTO("John Updated", "existing@example.com", null, null);

        User existingUser = new User("Another", "pass", "existing@example.com", fakeRole);
        existingUser.setId(UUID.randomUUID());

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));
        Mockito.when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        UserException exception = Assertions.assertThrows(UserException.class, () -> userService.update(userId, fakeUpdateDTO));

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("Deve lançar UserException ao tentar atualizar com role inexistente")
    void shouldThrowUserExceptionWhenUpdatingWithNonExistentRole() {
        UpdateUserRequestDTO fakeUpdateDTO = new UpdateUserRequestDTO("John Updated", "john.updated@example.com", null, 999L);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));
        Mockito.when(userRepository.findByEmail("john.updated@example.com")).thenReturn(Optional.empty());
        Mockito.when(roleRepository.findById(999L)).thenReturn(Optional.empty());

        UserException exception = Assertions.assertThrows(UserException.class, () -> userService.update(userId, fakeUpdateDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("Deve deletar um usuário com sucesso")
    void shouldDeleteUserSuccessfully() {
        UserDTO deletedUserDTO = new UserDTO(userId, "John", "john@example.com", fakeRole, false);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));
        Mockito.when(userMapper.toUserDTO(fakeUser)).thenReturn(deletedUserDTO);

        ResponseEntity<UserDTO> response = userService.delete(userId, new DeleteUserRequestDTO("Reason"));

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(deletedUserDTO, response.getBody());
        Assertions.assertFalse(fakeUser.getActive());
        Mockito.verify(userRepository).save(fakeUser);
        Mockito.verify(publisher, Mockito.times(2)).publishEvent(Mockito.any(Object.class));
    }

    @Test
    @DisplayName("Deve lançar UserException ao tentar deletar usuário inexistente")
    void shouldThrowUserExceptionWhenDeletingNonExistentUser() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserException exception = Assertions.assertThrows(UserException.class, () -> userService.delete(userId, new DeleteUserRequestDTO("Reason")));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("Deve processar o soft delete de uma Role e cascatear para os usuários")
    void shouldProcessRoleSoftDeleteSuccessfully() {
        Long roleIdToBeDeleted = 2L;
        RoleSoftDeleteObserver event = new RoleSoftDeleteObserver(roleIdToBeDeleted);

        List<UUID> affectedUsers = List.of(UUID.randomUUID(), UUID.randomUUID());

        Mockito.when(userRepository.cascadeDeleteFromRole(roleIdToBeDeleted)).thenReturn(affectedUsers);

        userService.onRoleSoftDelete(event);

        Mockito.verify(userRepository).cascadeDeleteFromRole(roleIdToBeDeleted);

        Mockito.verify(publisher, Mockito.times(4)).publishEvent(Mockito.any(Object.class));
    }

    @Test
    @DisplayName("Não deve publicar eventos se nenhum usuário for afetado pelo soft delete da Role")
    void shouldDoNothingWhenNoUsersAffectedByRoleSoftDelete() {
        Long roleIdToBeDeleted = 99L;
        RoleSoftDeleteObserver event = new RoleSoftDeleteObserver(roleIdToBeDeleted);

        Mockito.when(userRepository.cascadeDeleteFromRole(roleIdToBeDeleted)).thenReturn(List.of());

        userService.onRoleSoftDelete(event);

        Mockito.verify(userRepository).cascadeDeleteFromRole(roleIdToBeDeleted);
        Mockito.verify(publisher, Mockito.never()).publishEvent(Mockito.any());
    }

    @Test
    @DisplayName("Deve processar evento de recuperação de senha com sucesso")
    void shouldProcessRequestRecoveryEventSuccessfully() {
        String encodedToken = "encodedToken123";
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        RequestRecoveryObserver event = new RequestRecoveryObserver(fakeUser, encodedToken, expiresAt);

        userService.onRequestRecovery(event);

        Assertions.assertEquals(encodedToken, fakeUser.getResetToken());
        Assertions.assertEquals(expiresAt, fakeUser.getResetTokenExpiresAt());
        Mockito.verify(userRepository).save(fakeUser);
    }

    @Test
    @DisplayName("Deve processar evento de reset de senha com sucesso")
    void shouldProcessResetPasswordEventSuccessfully() {
        fakeUser.setResetToken("token123");
        fakeUser.setResetTokenExpiresAt(LocalDateTime.now().plusHours(1));

        String newPassword = "newPassword123";
        String encodedNewPassword = "encodedNewPassword123";
        ResetPasswordObserver event = new ResetPasswordObserver(fakeUser, newPassword);

        Mockito.when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

        userService.onResetPassword(event);

        Assertions.assertEquals(encodedNewPassword, fakeUser.getPassword());
        Assertions.assertNull(fakeUser.getResetToken());
        Assertions.assertNull(fakeUser.getResetTokenExpiresAt());
        Mockito.verify(passwordEncoder).encode(newPassword);
        Mockito.verify(userRepository).save(fakeUser);
    }
}
