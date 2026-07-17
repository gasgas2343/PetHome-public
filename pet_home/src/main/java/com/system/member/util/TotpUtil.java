package com.system.member.util;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import dev.samstevens.totp.util.Utils;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TotpUtil {


    public static String generateTotpSecret() {
        DefaultSecretGenerator secretGenerator = new DefaultSecretGenerator();
        return secretGenerator.generate();
    }

    public static Boolean verifyTotp(String secret, String code) {
        TimeProvider timeProvider = new SystemTimeProvider();
        DefaultCodeGenerator codeGenerator = new DefaultCodeGenerator();
        DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

        verifier.setTimePeriod(30);
        verifier.setAllowedTimePeriodDiscrepancy(1);

        return verifier.isValidCode(secret, code);
    }

    public static String buildQrCodeUrl(String email, String secret) {
        QrData data = new QrData.Builder()
                .label(email)
                .secret(secret)
                .issuer("毛起來")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        try {
            byte[] qrCodeImage = new ZxingPngQrGenerator().generate(data);
            return Utils.getDataUriForImage(qrCodeImage, "image/png");
        } catch (QrGenerationException e) {
            throw new RuntimeException("產生 TOTP QR Code 失敗", e);
        }
    }


}
