package org.wita.erp.controllers.user.authentication;

import dev.samstevens.totp.exceptions.QrGenerationException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.controllers.user.docs.AuthenticationDocs;
import org.wita.erp.domain.entities.user.dtos.*;
import org.wita.erp.services.user.UserService;
import org.wita.erp.services.user.authentication.AuthenticationService;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController implements AuthenticationDocs {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        return authenticationService.login(data);
    }

    @PostMapping("login/2fa/confirm")
    public ResponseEntity<LoginResponseDTO> twoFactorAuthenticationLogin(@RequestBody ConfirmTwoFactorAuthenticationRequestDTO data) {
        return authenticationService.twoFactorAuthenticationLogin(data);
    }

    @PostMapping("/recovery")
    public ResponseEntity<RecoveryResponseDTO> requestRecovery(@RequestBody @Valid RequestRecoveryDTO data, @RequestHeader(value = "User-Agent", required = false) String userAgent) throws MessagingException {
        return authenticationService.requestRecovery(data, userAgent);
    }

    @PostMapping("/reset")
    public ResponseEntity<RecoveryResponseDTO> resetPassword(@RequestBody @Valid RequestResetDTO data, @RequestParam("token") String token){
        return authenticationService.resetPassword(data, token);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid RegisterDTO data) {
        return userService.save(data);
    }

    @PostMapping("/2fa/enable")
    public ResponseEntity<TwoFactorAuthenticationRequestDTO> enableTwoFactorAuthentication(@RequestHeader(value = "User-Agent", required = false) String userAgent) throws QrGenerationException, MessagingException {
        return authenticationService.enableTwoFactorAuthentication(userAgent);
    }

    @PostMapping("/2fa/confirm")
    public ResponseEntity<UserDTO> confirmTwoFactorAuthentication(@RequestBody ConfirmTwoFactorAuthenticationRequestDTO data) {
        return authenticationService.confirmTwoFactorAuthentication(data);
    }

}
