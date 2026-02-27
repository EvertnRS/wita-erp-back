package org.wita.erp.infra.providers.twofactor;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.stereotype.Component;

@Component
public class TotpAuthenticationProvider implements TwoFactorAuthenticationProvider {
    private static final String ISSUER = "wita-erp";
    private final SecretGenerator secretGenerator;
    private final QrGenerator qrGenerator;
    private final CodeVerifier codeVerifier;

    public TotpAuthenticationProvider() {
        this.secretGenerator = new DefaultSecretGenerator();
        this.qrGenerator = new ZxingPngQrGenerator();

        CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1);
        TimeProvider timeProvider = new SystemTimeProvider();

        this.codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
    }

    @Override
    public String generateSecret() {
        return secretGenerator.generate();
    }

    @Override
    public byte[] generateQrCodeImage(String secret, String username)
            throws QrGenerationException {

        QrData data = new QrData.Builder()
                .label("MinhaApp:" + username)
                .secret(secret)
                .issuer(ISSUER)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        return qrGenerator.generate(data);
    }

    @Override
    public boolean validateCode(String secret, String code) {
        return codeVerifier.isValidCode(secret, code);
    }
}
