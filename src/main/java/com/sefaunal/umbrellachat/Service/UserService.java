package com.sefaunal.umbrellachat.Service;

import com.sefaunal.umbrellachat.Model.User;
import com.sefaunal.umbrellachat.Repository.UserRepository;
import com.sefaunal.umbrellachat.Request.VerificationRequest;
import com.sefaunal.umbrellachat.Response.GenericResponse;
import com.sefaunal.umbrellachat.Response.MFAResponse;
import com.sefaunal.umbrellachat.Util.CommonUtils;
import com.sefaunal.umbrellachat.Util.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author github.com/sefaunal
 * @since 2023-11-14
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final MFAService mfaService;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> findUserByMail(String userMail) {
        return userRepository.findByEmail(userMail);
    }

    public MFAResponse generateSecretKey() {
        User user = findUserByMail(CommonUtils.getUserInfo()).orElseThrow();

        // Generate a new secret key and show it to the user
        String secretKey = mfaService.generateNewSecret();
        String secretImageUri = mfaService.generateQrCodeImageUri(secretKey);

        // Update the user's MFA secret and save the user
        user.setMfaSecret(EncryptionUtils.encryptSecretKey(secretKey));
        saveUser(user);

        // Return the response with the secret key and image URI
        return MFAResponse.builder()
                .secret(secretKey)
                .secretImageUri(secretImageUri)
                .build();
    }

    public GenericResponse enableMFA(VerificationRequest verificationRequest) {
        User user = findUserByMail(CommonUtils.getUserInfo()).orElseThrow();

        // Decrypt the stored MFA secret key
        String encryptedSecretKey = user.getMfaSecret();
        String secretKey = EncryptionUtils.decryptSecretKey(encryptedSecretKey);

        // Validate the TOTP entered by the user
        boolean isTOTPValid = mfaService.isOtpValid(secretKey, verificationRequest.getMfaCode());

        if (!isTOTPValid) {
            throw new IllegalArgumentException("Invalid TOTP. MFA setup failed.");
        }

        user.setMfaEnabled(true);
        saveUser(user);

        return new GenericResponse(200, "MFA has been successfully enabled.");
    }

    public GenericResponse disableMFA() {
        User user = findUserByMail(CommonUtils.getUserInfo()).orElseThrow();
        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        saveUser(user);

        return new GenericResponse(200, "MFA has been successfully disabled.");
    }
}
