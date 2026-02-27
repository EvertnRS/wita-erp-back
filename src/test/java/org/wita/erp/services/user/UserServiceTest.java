package org.wita.erp.services.user;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.entities.user.authentication.dtos.VerifyEmailResponseDTO;
import org.wita.erp.domain.entities.user.dtos.DeleteUserRequestDTO;
import org.wita.erp.domain.entities.user.dtos.RegisterDTO;
import org.wita.erp.domain.entities.user.dtos.UpdateUserRequestDTO;
import org.wita.erp.domain.entities.user.dtos.UserDTO;
import org.wita.erp.domain.entities.user.mappers.UserMapper;
import org.wita.erp.domain.entities.user.role.Role;
import org.wita.erp.domain.repositories.user.UserRepository;
import org.wita.erp.domain.repositories.user.role.RoleRepository;
import org.wita.erp.infra.exceptions.user.UserException;
import org.wita.erp.infra.providers.email.EmailProvider;
import org.wita.erp.services.user.authentication.observers.RecoveryRequestObserver;
import org.wita.erp.services.user.authentication.observers.ResetPasswordObserver;
import org.wita.erp.services.user.role.observers.RoleSoftDeleteObserver;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


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
    private EmailProvider emailProvider;
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

    private void mockAuthenticatedUser() {
        User loggedUser = new User();
        loggedUser.setId(UUID.randomUUID());
        loggedUser.setName("Admin");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(loggedUser);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("Deve retornar todos os usuários quando o searchTerm for nulo")
    void shouldReturnAllUsersWhenSearchTermIsNull() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> fakePage = new PageImpl<>(List.of(fakeUser));

        when(userRepository.findAll(pageable)).thenReturn(fakePage);
        when(userMapper.toUserDTO(fakeUser)).thenReturn(baseUserDTO);

        ResponseEntity<Page<UserDTO>> response = userService.getAllUsers(pageable, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(userRepository).findAll(pageable);
        verify(userRepository, Mockito.never()).findBySearchTerm(any(), any());
    }

    @Test
    @DisplayName("Deve retornar usuários filtrados pelo searchTerm")
    void shouldReturnUsersFilteredBySearchTerm() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> fakePage = new PageImpl<>(List.of(fakeUser));

        when(userRepository.findBySearchTerm("john", pageable)).thenReturn(fakePage);
        when(userMapper.toUserDTO(fakeUser)).thenReturn(baseUserDTO);

        ResponseEntity<Page<UserDTO>> response = userService.getAllUsers(pageable, "john");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(userRepository).findBySearchTerm("john", pageable);
        verify(userRepository, Mockito.never()).findAll(any(Pageable.class));
    }

    @Test
    void shouldRegisterUserAndSendVerificationEmail() throws Exception {
        mockAuthenticatedUser();
        Long roleId = 1L;
        Role role = new Role();
        role.setId(roleId);

        RegisterDTO dto = new RegisterDTO("john@example.com", "John", roleId);

        when(userRepository.findByEmail(dto.email()))
                .thenReturn(Optional.empty());

        when(roleRepository.findById(roleId))
                .thenReturn(Optional.of(role));

        when(emailProvider.buildVerifyEmailTemplate(
                any(), any(), any(), any(), any(), any()))
                .thenReturn("<html>email</html>");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        ResponseEntity<VerifyEmailResponseDTO> response =
                userService.save(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(dto.name(), savedUser.getName());
        assertEquals(dto.email(), savedUser.getEmail());
        assertEquals(role, savedUser.getRole());
        assertNotNull(savedUser.getVerifyEmailToken());
        assertNotNull(savedUser.getVerifyEmailTokenExpiresAt());

        verify(emailProvider).sendEmail(
                eq(dto.email()),
                eq("Verificação de Email"),
                eq("<html>email</html>")
        );
    }

    @Test
    @DisplayName("Deve lançar UserException ao tentar cadastrar usuário com email já existente")
    void shouldThrowUserExceptionWhenEmailAlreadyRegistered() throws MessagingException {
        RegisterDTO dto = new RegisterDTO("john@example.com", "John", 2L);
        User existingUser = new User();
        existingUser.setEmail("john@example.com");

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(existingUser));

        UserException exception = assertThrows(
                UserException.class,
                () -> userService.save(dto)
        );

        assertAll(
                () -> assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus()),
                () -> assertEquals("Email already registered", exception.getMessage())
        );

        verify(userRepository).findByEmail("john@example.com");
        verify(userRepository, never()).save(any());
        verify(roleRepository, never()).findById(any());
        verify(emailProvider, never()).sendEmail(any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar UserException quando role não existir")
    void shouldThrowUserExceptionWhenRoleNotFound() throws MessagingException {
        mockAuthenticatedUser();
        RegisterDTO dto = new RegisterDTO("john@example.com", "John", 2L);

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.empty());

        when(roleRepository.findById(2L))
                .thenReturn(Optional.empty());

        UserException exception = assertThrows(
                UserException.class,
                () -> userService.save(dto)
        );

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus()),
                () -> assertEquals("Role not registered in the system", exception.getMessage())
        );

        verify(userRepository).findByEmail("john@example.com");
        verify(roleRepository).findById(2L);
        verify(userRepository, never()).save(any());
        verify(emailProvider, never()).sendEmail(any(), any(), any());
    }

    @Test
    @DisplayName("Deve atualizar um usuário com sucesso")
    void shouldUpdateUserSuccessfully() {
        UpdateUserRequestDTO fakeUpdateDTO = new UpdateUserRequestDTO("John Updated", "john.updated@example.com", null, null);
        UserDTO expectedUserDTO = new UserDTO(userId, "John Updated", "john.updated@example.com", fakeRole, true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));
        when(userRepository.findByEmail(fakeUpdateDTO.email())).thenReturn(Optional.empty());
        when(userMapper.toUserDTO(fakeUser)).thenReturn(expectedUserDTO);

        ResponseEntity<UserDTO> response = userService.update(userId, fakeUpdateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserDTO, response.getBody());
        verify(userRepository).save(fakeUser);
    }

    @Test
    @DisplayName("Deve atualizar usuário com nova senha com sucesso")
    void shouldUpdateUserWithPasswordSuccessfully() {
        UpdateUserRequestDTO fakeUpdateDTO = new UpdateUserRequestDTO("John Updated", "john.updated@example.com", "newPassword", null);
        UserDTO expectedUserDTO = new UserDTO(userId, "John Updated", "john.updated@example.com", fakeRole, true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));
        when(userRepository.findByEmail(fakeUpdateDTO.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userMapper.toUserDTO(fakeUser)).thenReturn(expectedUserDTO);

        ResponseEntity<UserDTO> response = userService.update(userId, fakeUpdateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserDTO, response.getBody());
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(fakeUser);
    }

    @Test
    @DisplayName("Deve atualizar usuário com novo role com sucesso")
    void shouldUpdateUserWithRoleSuccessfully() {
        UpdateUserRequestDTO fakeUpdateDTO = new UpdateUserRequestDTO("John Updated", "john.updated@example.com", null, 3L);
        Role newRole = new Role(3L, "ADMIN", true, null);
        UserDTO expectedUserDTO = new UserDTO(userId, "John Updated", "john.updated@example.com", newRole, true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));
        when(userRepository.findByEmail(fakeUpdateDTO.email())).thenReturn(Optional.empty());
        when(roleRepository.findById(3L)).thenReturn(Optional.of(newRole));
        when(userMapper.toUserDTO(fakeUser)).thenReturn(expectedUserDTO);

        ResponseEntity<UserDTO> response = userService.update(userId, fakeUpdateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserDTO, response.getBody());
        verify(roleRepository).findById(3L);
        verify(userRepository).save(fakeUser);
    }

    @Test
    @DisplayName("Deve lançar UserException ao tentar atualizar usuário inexistente")
    void shouldThrowUserExceptionWhenUpdatingNonExistentUser() {
        UpdateUserRequestDTO fakeUpdateDTO = new UpdateUserRequestDTO("John Updated", "john.updated@example.com", null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserException exception = Assertions.assertThrows(UserException.class, () -> userService.update(userId, fakeUpdateDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar UserException ao tentar atualizar email para um já registrado")
    void shouldThrowUserExceptionWhenUpdatingWithExistingEmail() {
        UpdateUserRequestDTO fakeUpdateDTO = new UpdateUserRequestDTO("John Updated", "existing@example.com", null, null);

        User existingUser = new User("Another", "pass", "existing@example.com", fakeRole);
        existingUser.setId(UUID.randomUUID());

        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        UserException exception = Assertions.assertThrows(UserException.class, () -> userService.update(userId, fakeUpdateDTO));

        assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar UserException ao tentar atualizar com role inexistente")
    void shouldThrowUserExceptionWhenUpdatingWithNonExistentRole() {
        UpdateUserRequestDTO fakeUpdateDTO = new UpdateUserRequestDTO("John Updated", "john.updated@example.com", null, 999L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));
        when(userRepository.findByEmail("john.updated@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findById(999L)).thenReturn(Optional.empty());

        UserException exception = Assertions.assertThrows(UserException.class, () -> userService.update(userId, fakeUpdateDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve deletar um usuário com sucesso")
    void shouldDeleteUserSuccessfully() {
        UserDTO deletedUserDTO = new UserDTO(userId, "John", "john@example.com", fakeRole, false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));
        when(userMapper.toUserDTO(fakeUser)).thenReturn(deletedUserDTO);

        ResponseEntity<UserDTO> response = userService.delete(userId, new DeleteUserRequestDTO("Reason"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(deletedUserDTO, response.getBody());
        Assertions.assertFalse(fakeUser.getActive());
        verify(userRepository).save(fakeUser);
        verify(publisher, Mockito.times(2)).publishEvent(any(Object.class));
    }

    @Test
    @DisplayName("Deve lançar UserException ao tentar deletar usuário inexistente")
    void shouldThrowUserExceptionWhenDeletingNonExistentUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserException exception = Assertions.assertThrows(UserException.class, () -> userService.delete(userId, new DeleteUserRequestDTO("Reason")));

        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve processar o soft delete de uma Role e cascatear para os usuários")
    void shouldProcessRoleSoftDeleteSuccessfully() {
        Long roleIdToBeDeleted = 2L;
        RoleSoftDeleteObserver event = new RoleSoftDeleteObserver(roleIdToBeDeleted);

        List<UUID> affectedUsers = List.of(UUID.randomUUID(), UUID.randomUUID());

        when(userRepository.cascadeDeleteFromRole(roleIdToBeDeleted)).thenReturn(affectedUsers);

        userService.onRoleSoftDelete(event);

        verify(userRepository).cascadeDeleteFromRole(roleIdToBeDeleted);

        verify(publisher, Mockito.times(4)).publishEvent(any(Object.class));
    }

    @Test
    @DisplayName("Não deve publicar eventos se nenhum usuário for afetado pelo soft delete da Role")
    void shouldDoNothingWhenNoUsersAffectedByRoleSoftDelete() {
        Long roleIdToBeDeleted = 99L;
        RoleSoftDeleteObserver event = new RoleSoftDeleteObserver(roleIdToBeDeleted);

        when(userRepository.cascadeDeleteFromRole(roleIdToBeDeleted)).thenReturn(List.of());

        userService.onRoleSoftDelete(event);

        verify(userRepository).cascadeDeleteFromRole(roleIdToBeDeleted);
        verify(publisher, Mockito.never()).publishEvent(any());
    }

    @Test
    @DisplayName("Deve processar evento de recuperação de senha com sucesso")
    void shouldProcessRequestRecoveryEventSuccessfully() {
        String encodedToken = "encodedToken123";
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        RecoveryRequestObserver event = new RecoveryRequestObserver(fakeUser, encodedToken, expiresAt);

        userService.onRequestRecovery(event);

        assertEquals(encodedToken, fakeUser.getResetToken());
        assertEquals(expiresAt, fakeUser.getResetTokenExpiresAt());
        verify(userRepository).save(fakeUser);
    }

    @Test
    @DisplayName("Deve processar evento de reset de senha com sucesso")
    void shouldProcessResetPasswordEventSuccessfully() {
        fakeUser.setResetToken("token123");
        fakeUser.setResetTokenExpiresAt(LocalDateTime.now().plusHours(1));

        String newPassword = "newPassword123";
        String encodedNewPassword = "encodedNewPassword123";
        ResetPasswordObserver event = new ResetPasswordObserver(fakeUser, newPassword);

        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

        userService.onResetPassword(event);

        assertEquals(encodedNewPassword, fakeUser.getPassword());
        Assertions.assertNull(fakeUser.getResetToken());
        Assertions.assertNull(fakeUser.getResetTokenExpiresAt());
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(fakeUser);
    }
}