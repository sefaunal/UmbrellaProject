package com.sefaunal.umbrellasecurity.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author github.com/sefaunal
 * @since 2023-12-18
 */
public class EncryptionUtils {
    private static final Logger LOG = LoggerFactory.getLogger(EncryptionUtils.class);

    private static final String SECRET_KEY_ALGORITHM = "AES";

    private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private static final String SECRET_KEY = "d3h?*bU1*UKe?y9&lx**3&r6n?$ICR8w!c#8cHOwEkUv#nlyls3th@c7I$&at#-*";

    public static List<String> encryptRecoveryCodes(List<String> recoveryCodes) {
        List<String> encryptedRecoveryCodes = new ArrayList<>();

        try {
            SecretKey secretKey = generateSecretKey();
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            for (String code : recoveryCodes) {
                byte[] encryptedBytes = cipher.doFinal(code.getBytes());
                encryptedRecoveryCodes.add(Base64.getEncoder().encodeToString(encryptedBytes));
            }
        } catch (Exception e) {
            LOG.error("Error occurred during encrypting recovery codes: " + e.getMessage());
        }

        return encryptedRecoveryCodes;
    }

    public static List<String> decryptRecoveryCodes(List<String> encryptedRecoveryCodes) {
        List<String> decryptedRecoveryCodes = new ArrayList<>();

        try {
            SecretKey secretKey = generateSecretKey();
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            for (String code : encryptedRecoveryCodes) {
                byte[] encryptedBytes = Base64.getDecoder().decode(code);
                decryptedRecoveryCodes.add(new String(cipher.doFinal(encryptedBytes)));
            }
        } catch (Exception e) {
            LOG.error("Error occurred during decrypting recovery codes: " + e.getMessage());
        }

        return decryptedRecoveryCodes;
    }

    public static String encryptSecretKey(String mfaSecretKey) {
        String encryptedSecretKey = null;

        try {
            SecretKey secretKey = generateSecretKey();
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedBytes = cipher.doFinal(mfaSecretKey.getBytes());
            encryptedSecretKey = Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            LOG.error("Error occurred during encrypting MFA Secret Key: " + e.getMessage());
        }

        return encryptedSecretKey;
    }

    public static String decryptSecretKey(String encryptedSecretKey) {
        String decryptedSecretKey = null;

        try {
            SecretKey secretKey = generateSecretKey();
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedSecretKey);
            decryptedSecretKey = new String(cipher.doFinal(encryptedBytes));
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return decryptedSecretKey;
    }

    private static SecretKey generateSecretKey() throws Exception {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec keySpec = new PBEKeySpec(SECRET_KEY.toCharArray(), "salt".getBytes(), 1024, 256);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return new SecretKeySpec(secretKey.getEncoded(), SECRET_KEY_ALGORITHM);
    }
}
