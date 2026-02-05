package org.wita.erp.infra.providers.auth;

import org.wita.erp.domain.entities.user.User;

public interface AuthProvider {
    String generateToken(User user);
    String validateToken(String token);

}
