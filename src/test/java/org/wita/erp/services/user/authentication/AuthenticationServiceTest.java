package org.wita.erp.services.user.authentication;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.entities.user.dtos.*;
import org.wita.erp.domain.entities.user.mappers.UserMapper;
import org.wita.erp.domain.entities.user.role.Role;
import org.wita.erp.domain.repositories.user.UserRepository;
import org.wita.erp.infra.exceptions.auth.AuthException;
import org.wita.erp.infra.exceptions.user.UserException;
import org.wita.erp.infra.providers.auth.AuthProvider;
import org.wita.erp.infra.providers.email.EmailProvider;
import org.wita.erp.services.user.authentication.observers.RequestRecoveryObserver;

import java.util.HashSet;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AuthProvider authProvider;
    @Mock
    private EmailProvider emailProvider;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User defaultUser;
    private UserDTO defaultUserDTO;
    private AuthenticationDTO defaultAuthRequest;
    private RequestRecoveryDTO defaultRecoveryRequest;
    private final String defaultUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)";

    @BeforeEach
    void setUp() {
        defaultUser = new User("Admin", "senha123", "admin@example.com", null);

        defaultUserDTO = new UserDTO(null, "Admin", "admin@example.com", new Role(1L, "ADMIN", true, new HashSet<>()), null);

        defaultAuthRequest = new AuthenticationDTO("admin@example.com", "senha123");
        defaultRecoveryRequest = new RequestRecoveryDTO("admin@example.com");
    }

    @Test
    @DisplayName("Deve realizar login com sucesso e retornar o Token JWT")
    void shouldLoginSuccessfully() {
        String fakeJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        Authentication mockAuthentication = Mockito.mock(Authentication.class);

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        Mockito.when(mockAuthentication.getPrincipal()).thenReturn(defaultUser);
        Mockito.when(authProvider.generateToken(defaultUser)).thenReturn(fakeJwtToken);
        Mockito.when(userMapper.toUserDTO(defaultUser)).thenReturn(defaultUserDTO);

        ResponseEntity<LoginResponseDTO> response = authenticationService.login(defaultAuthRequest);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(fakeJwtToken, response.getBody().token());
        Assertions.assertEquals(defaultUserDTO, response.getBody().user());

        Mockito.verify(authenticationManager).authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));
        Mockito.verify(authProvider).generateToken(defaultUser);
    }

    @Test
    @DisplayName("Deve lançar exceção quando as credenciais forem inválidas")
    void shouldThrowExceptionWhenCredentialsAreInvalid() {
        AuthenticationDTO invalidAuthData = new AuthenticationDTO("admin@example.com", "senha-errada");

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthException("Bad credentials", HttpStatus.UNAUTHORIZED) {
                });

        Assertions.assertThrows(AuthException.class,
                () -> authenticationService.login(invalidAuthData));

        Mockito.verify(authProvider, Mockito.never()).generateToken(Mockito.any());
    }

    @Test
    @DisplayName("Deve processar a solicitação de recuperação de senha com sucesso")
    void shouldRequestRecoverySuccessfully() throws MessagingException {
        String fakeTemplate = "<html>Template de Email Mockado</html>";
        String fakeEncodedToken = "ENCODED_HASH_123";

        Mockito.when(userRepository.findByEmail(defaultRecoveryRequest.email())).thenReturn(Optional.of(defaultUser));
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(fakeEncodedToken);
        Mockito.when(emailProvider.buildRecoveryPasswordTemplate(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()
        )).thenReturn(fakeTemplate);

        ResponseEntity<RecoveryDTO> response = authenticationService.requestRecovery(defaultRecoveryRequest, defaultUserAgent);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().message().contains("A password recovery email has been sent to " + defaultRecoveryRequest.email()));

        Mockito.verify(userRepository).findByEmail(defaultRecoveryRequest.email());
        Mockito.verify(passwordEncoder).encode(Mockito.anyString());
        Mockito.verify(emailProvider).sendEmail(defaultRecoveryRequest.email(), "Redefinição de senha", fakeTemplate);

        ArgumentCaptor<RequestRecoveryObserver> eventCaptor = ArgumentCaptor.forClass(RequestRecoveryObserver.class);
        Mockito.verify(publisher).publishEvent(eventCaptor.capture());

        RequestRecoveryObserver capturedEvent = eventCaptor.getValue();
        Assertions.assertEquals(defaultUser, capturedEvent.user());
        Assertions.assertEquals(fakeEncodedToken, capturedEvent.encodedToken());
    }

    @Test
    @DisplayName("Deve lançar UserException ao tentar recuperar senha de um e-mail não cadastrado")
    void shouldThrowUserExceptionWhenEmailNotFound() throws MessagingException {
        Mockito.when(userRepository.findByEmail(defaultRecoveryRequest.email())).thenReturn(Optional.empty());

        UserException exception = Assertions.assertThrows(UserException.class,
                () -> authenticationService.requestRecovery(defaultRecoveryRequest, defaultUserAgent));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.anyString());
        Mockito.verify(emailProvider, Mockito.never()).sendEmail(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(publisher, Mockito.never()).publishEvent(Mockito.any());
    }

    @Test
    @DisplayName("Não deve publicar o evento de recuperação se o envio do e-mail falhar")
    void shouldNotPublishEventIfEmailSendingFails() throws MessagingException {
        Mockito.when(userRepository.findByEmail(defaultRecoveryRequest.email())).thenReturn(Optional.of(defaultUser));
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("HASH");
        Mockito.when(emailProvider.buildRecoveryPasswordTemplate(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()
        )).thenReturn("<html>Template</html>");

        Mockito.doThrow(new MessagingException("Timeout do SMTP")).when(emailProvider)
                .sendEmail(Mockito.eq(defaultRecoveryRequest.email()), Mockito.anyString(), Mockito.anyString());

        Assertions.assertThrows(MessagingException.class,
                () -> authenticationService.requestRecovery(defaultRecoveryRequest, defaultUserAgent));

        Mockito.verify(publisher, Mockito.never()).publishEvent(Mockito.any());
    }
}
