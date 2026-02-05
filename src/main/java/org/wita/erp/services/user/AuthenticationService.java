package org.wita.erp.services.user;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.user.dtos.AuthenticationDTO;
import org.wita.erp.domain.entities.user.dtos.LoginResponseDTO;
import org.wita.erp.domain.entities.user.dtos.RequestRecoveryDTO;
import org.wita.erp.domain.entities.user.dtos.RequestResetDTO;
import org.wita.erp.domain.entities.user.mappers.UserMapper;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.repositories.user.UserRepository;
import org.wita.erp.infra.exceptions.user.UserException;
import org.wita.erp.infra.providers.auth.AuthProvider;
import org.wita.erp.infra.providers.email.EmailProvider;
import org.wita.erp.services.user.observers.RequestRecoveryObserver;
import org.wita.erp.services.user.observers.ResetPasswordObserver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final AuthProvider authProvider;
    private final EmailProvider emailProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher publisher;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public ResponseEntity<LoginResponseDTO> login(AuthenticationDTO data) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(userNamePassword);

        var user =  (User) auth.getPrincipal();
        var token = authProvider.generateToken(user);

        return ResponseEntity.ok(new LoginResponseDTO(userMapper.toUserDTO(user), token));
    }

    public ResponseEntity<String> requestRecovery(RequestRecoveryDTO data, String userAgent) throws MessagingException {
        User user = userRepository.findByEmail(data.email())
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        String rawToken =  UUID.randomUUID().toString();
        String encodedToken = passwordEncoder.encode(rawToken);

        String browser = getBrowserInfo(userAgent);
        String device = getDeviceInfo(userAgent);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String template = emailProvider.buildTemplate("Recuperação de Senha",
                "Olá " + user.getName() + ", recebemos um pedido para redefinir sua senha.",
                browser,
                device,
                user.getName(),
                LocalDateTime.now().format(formatter),
                "Redefinir Senha",
                frontendUrl + "auth/reset"  + "?token=" + rawToken);

        emailProvider.sendEmail(data.email(), "Redefinição de senha", template);

        publisher.publishEvent(new RequestRecoveryObserver(user, encodedToken, expiresAt));

        return ResponseEntity.ok("Reset link sent");
    }

    public ResponseEntity<String> resetPassword(RequestResetDTO data, String token){
        List<User> users = userRepository.findAllByResetTokenIsNotNull();

        User user = users.stream()
                .filter(u -> passwordEncoder.matches(token, u.getResetToken()))
                .findFirst()
                .orElseThrow(() ->
                        new UserException("Invalid reset token", HttpStatus.BAD_REQUEST));

        if(user.getResetTokenExpiresAt().isBefore(LocalDateTime.now())){
            throw new UserException("Reset token has expired", HttpStatus.BAD_REQUEST);
        }

        publisher.publishEvent(new ResetPasswordObserver(user, data.password()));

        return ResponseEntity.ok("Password reset successfully");
    }

    private String getBrowserInfo(String userAgent) {
        return  userAgent.contains("Chrome") ? "Chrome" :
                userAgent.contains("Firefox") ? "Firefox" :
                userAgent.contains("Safari") && !userAgent.contains("Chrome") ? "Safari" :
                userAgent.contains("Edg") ? "Edge" :
                "Desconhecido";
    }

    private String getDeviceInfo(String userAgent){
        return  userAgent.contains("Mobile") ? "Mobile" :
                userAgent.contains("Tablet") ? "Tablet" :
                "Desktop";
    }
}
