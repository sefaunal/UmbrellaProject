package com.sefaunal.umbrellachat.Service;

import com.sefaunal.umbrellachat.Model.User;
import com.sefaunal.umbrellachat.Repository.UserRepository;
import com.sefaunal.umbrellachat.Request.VerificationRequest;
import com.sefaunal.umbrellachat.Response.GenericResponse;
import com.sefaunal.umbrellachat.Response.MFAResponse;
import com.sefaunal.umbrellachat.Util.CommonUtils;
import com.sefaunal.umbrellachat.Util.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public User findUserByMail(String userMail) {
        return userRepository.findByEmail(userMail)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with " + userMail));
    }

    public Optional<User> findByOauth2ID(String findByOauth2ID) {
        return userRepository.findByOauth2ID(findByOauth2ID);
    }

    public boolean isEmailFree(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    public boolean isEmailInUse(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isUsernameFree(String username) {
        return userRepository.findByUsername(username).isEmpty();
    }

    public boolean isUsernameInUse(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public MFAResponse generateSecretKey() {
        User user = findUserByMail(CommonUtils.getUserInfo());

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
        User user = findUserByMail(CommonUtils.getUserInfo());

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
        User user = findUserByMail(CommonUtils.getUserInfo());
        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        saveUser(user);

        return new GenericResponse(200, "MFA has been successfully disabled.");
    }
}
