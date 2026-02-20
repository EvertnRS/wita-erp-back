package org.wita.erp.infra.providers.twofactor;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class TwoFactorAuthenticationToken extends AbstractAuthenticationToken {

    private final UserDetails principal;

    public TwoFactorAuthenticationToken(UserDetails principal) {
        super(null);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}

