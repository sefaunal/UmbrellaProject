package com.sefaunal.umbrellachat.Service;

import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import dev.samstevens.totp.util.Utils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author github.com/sefaunal
 * @since 2023-12-14
 */
@Service
@RequiredArgsConstructor
public class MFAService {
    private static final Logger LOG = LoggerFactory.getLogger(MFAService.class);

    public String generateNewSecret() {
        return new DefaultSecretGenerator(64).generate();
    }

    public String generateQrCodeImageUri(String secret) {
        QrData qrData = new QrData.Builder()
                .label("Umbrella Chat")
                .secret(secret)
                .issuer("Umbrella Corp.")
                .algorithm(HashingAlgorithm.SHA256)
                .digits(6)
                .period(30)
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData = new byte[0];

        try {
            imageData = generator.generate(qrData);
        } catch (QrGenerationException e) {
            LOG.error("Error while generating QR: " + e.getMessage());
        }

        return Utils.getDataUriForImage(imageData, generator.getImageMimeType());
    }

    public boolean isOtpValid(String secret, String code) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

        return codeVerifier.isValidCode(secret, code);
    }
}
