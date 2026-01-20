package org.wita.erp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.User.Dtos.AuthenticationDTO;
import org.wita.erp.domain.User.Dtos.LoginResponseDTO;
import org.wita.erp.domain.User.Mappers.UserMapper;
import org.wita.erp.domain.User.User;
import org.wita.erp.infra.security.TokenService;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserMapper userMapper;

    public ResponseEntity<LoginResponseDTO> login(AuthenticationDTO data) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(userNamePassword);

        var user =  (User) auth.getPrincipal();
        var token = tokenService.generateToken(user);

        return ResponseEntity.ok(new LoginResponseDTO(userMapper.toUserDTO(user), token));
    }
}
