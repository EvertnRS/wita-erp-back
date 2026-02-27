package org.wita.erp.infra.providers.twofactor;

import dev.samstevens.totp.exceptions.QrGenerationException;

public interface TwoFactorAuthenticationProvider {

    String generateSecret();

    byte[] generateQrCodeImage(String secret, String username) throws QrGenerationException;

    boolean validateCode(String secret, String code);
}
