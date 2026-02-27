package org.wita.erp.infra.providers.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.wita.erp.domain.entities.user.User;

public interface AuthProvider {
    String generateToken(User user);
    String generateTwoFactorToken(User user);
    String validateToken(String token);
    DecodedJWT decodeToken(String token);

}
