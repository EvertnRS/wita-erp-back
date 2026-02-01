package org.wita.erp.infra.providers.auth;

import org.wita.erp.domain.entities.user.User;

public interface AuthProvider {
    public String generateToken(User user);
    public String validateToken(String token);

}
