package org.wita.erp.services.user.authentication;

import dev.samstevens.totp.exceptions.QrGenerationException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.entities.user.authentication.UserAuthentication;
import org.wita.erp.domain.entities.user.dtos.*;
import org.wita.erp.domain.entities.user.mappers.UserMapper;
import org.wita.erp.domain.repositories.user.UserRepository;
import org.wita.erp.domain.repositories.user.authentication.UserAuthenticationRepository;
import org.wita.erp.infra.exceptions.auth.AuthException;
import org.wita.erp.infra.exceptions.user.UserException;
import org.wita.erp.infra.providers.auth.AuthProvider;
import org.wita.erp.infra.providers.email.EmailProvider;
import org.wita.erp.infra.providers.twofactor.TwoFactorAuthenticationProvider;
import org.wita.erp.infra.providers.twofactor.TwoFactorAuthenticationToken;
import org.wita.erp.services.user.authentication.observers.RequestRecoveryObserver;
import org.wita.erp.services.user.authentication.observers.ResetPasswordObserver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserAuthenticationRepository userAuthenticationRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthProvider authProvider;
    private final EmailProvider emailProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TwoFactorAuthenticationProvider twoFactorAuthenticationProvider;
    private final ApplicationEventPublisher publisher;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public ResponseEntity<LoginResponseDTO> login(AuthenticationDTO data) {
        Authentication auth;
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());

        try {
            auth = this.authenticationManager.authenticate(userNamePassword);
        } catch (AuthenticationException e) {
            throw new AuthException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        var user = (User) auth.getPrincipal();
        var userAuth = userAuthenticationRepository.findByUserId(user.getId())
                .orElse(null);

        if (userAuth != null && userAuth.getSecret() != null) {
            var twoFactorToken = authProvider.generateTwoFactorToken(user);

            return ResponseEntity.ok(new LoginResponseDTO(userMapper.toUserDTO(user), twoFactorToken, true));
        }

        var token = authProvider.generateToken(user);

        return ResponseEntity.ok(new LoginResponseDTO(userMapper.toUserDTO(user), token, false));
    }

    public ResponseEntity<RecoveryResponseDTO> requestRecovery(RequestRecoveryDTO data, String userAgent) throws MessagingException {
        User user = userRepository.findByEmail(data.email())
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        String rawToken =  UUID.randomUUID().toString();
        String encodedToken = passwordEncoder.encode(rawToken);

        String browser = getBrowserInfo(userAgent);
        String device = getDeviceInfo(userAgent);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String template = emailProvider.buildRecoveryPasswordTemplate(
                "Recuperação de Senha",
                "Olá " + user.getName() + ", recebemos um pedido para redefinir sua senha.",
                browser,
                device,
                user.getName(),
                LocalDateTime.now().format(formatter),
                "Redefinir Senha",
                frontendUrl + "auth/reset"  + "?token=" + rawToken);

        emailProvider.sendEmail(data.email(), "Redefinição de senha", template);

        publisher.publishEvent(new RequestRecoveryObserver(user, encodedToken, expiresAt));

        return ResponseEntity.ok(new RecoveryResponseDTO("A password recovery email has been sent to " + data.email() + " if it is registered in our system. The link will expire in 15 minutes."));
    }

    public ResponseEntity<RecoveryResponseDTO> resetPassword(RequestResetDTO data, String token){
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

        return ResponseEntity.ok(new RecoveryResponseDTO("Password reset successfully"));
    }

    public ResponseEntity<TwoFactorAuthenticationRequestDTO> enableTwoFactorAuthentication(String userAgent) throws QrGenerationException, MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        if (userAuthenticationRepository.findByUserId(user.getId()).isPresent()) {
            throw new AuthException("2FA is already enabled for this account", HttpStatus.BAD_REQUEST);
        }

        String tempSecret = twoFactorAuthenticationProvider.generateSecret();

        UserAuthentication userAuth = new UserAuthentication();
        userAuth.setUser(user);
        userAuth.setTemporarySecret(tempSecret);
        userAuth.setTemporarySecretExpiration(LocalDateTime.now().plusMinutes(10));
        userAuthenticationRepository.save(userAuth);

        byte[] qrCode = twoFactorAuthenticationProvider.generateQrCodeImage(
                tempSecret,
                user.getUsername()
        );

        String browser = getBrowserInfo(userAgent);
        String device = getDeviceInfo(userAgent);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String template = emailProvider.buildEnable2FATemplate(
                "Autenticação de dois fatores",
                "Olá " + user.getName() + ", recebemos um pedido para habilitar a autenticação de dois fatores na sua conta. " +
                        "Use o aplicativo de autenticação da sua preferência e aponte para o qr code a fim de finalizar a configuração.",
                browser,
                device,
                user.getName(),
                LocalDateTime.now().format(formatter));

        emailProvider.sendEmail(user.getEmail(), "Autenticação de dois fatores", template, qrCode);

        return ResponseEntity.ok(new TwoFactorAuthenticationRequestDTO("Email 2FA request has been sent successfully to " + user.getEmail() + ". The request will expire in 10 minutes."));
    }

    public ResponseEntity<UserDTO> confirmTwoFactorAuthentication(ConfirmTwoFactorAuthenticationRequestDTO data) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        UserAuthentication userAuth = userAuthenticationRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AuthException("User authentication factor not found", HttpStatus.BAD_REQUEST));

        if (userAuth.getTemporarySecretExpiration() != null && userAuth.getTemporarySecretExpiration().isBefore(LocalDateTime.now())) {
            throw new AuthException("No 2FA setup in progress", HttpStatus.BAD_REQUEST);
        }

        boolean isValid = twoFactorAuthenticationProvider.validateCode(userAuth.getTemporarySecret(), data.code());

        if (!isValid) {
            throw new AuthException("2FA code invalid", HttpStatus.BAD_REQUEST);
        }

        userAuth.setSecret(userAuth.getTemporarySecret());
        userAuth.setTemporarySecret(null);
        userAuth.setTemporarySecretExpiration(null);
        userAuthenticationRepository.save(userAuth);

        return ResponseEntity.ok(userMapper.toUserDTO(user));
    }

    public ResponseEntity<LoginResponseDTO> twoFactorAuthenticationLogin(ConfirmTwoFactorAuthenticationRequestDTO data) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof TwoFactorAuthenticationToken)) {
            throw new AuthException("Invalid authentication state",
                    HttpStatus.FORBIDDEN);
        }

        User user = (User) authentication.getPrincipal();
        UserAuthentication userAuth = userAuthenticationRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AuthException("User authentication factor not found", HttpStatus.BAD_REQUEST));

        boolean isValid = twoFactorAuthenticationProvider.validateCode(userAuth.getSecret(), data.code());

        if(!isValid) {
            throw new AuthException("Invalid 2FA code", HttpStatus.BAD_REQUEST);
        }

        var token = authProvider.generateToken(user);

        return ResponseEntity.ok(new LoginResponseDTO(userMapper.toUserDTO(user), token, false));
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
