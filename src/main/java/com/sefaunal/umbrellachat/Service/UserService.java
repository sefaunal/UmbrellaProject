package com.sefaunal.umbrellachat.Service;

import com.sefaunal.umbrellachat.Model.User;
import com.sefaunal.umbrellachat.Repository.UserRepository;
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

    public MFAResponse enableMFA() {
        User user = findUserByMail(CommonUtils.getUserInfo()).orElseThrow();
        user.setMfaEnabled(true);
        user.setMfaSecret(EncryptionUtils.encryptSecretKey(mfaService.generateNewSecret()));
        saveUser(user);

        return MFAResponse.builder()
                .secret(EncryptionUtils.decryptSecretKey(user.getMfaSecret()))
                .secretImageUri(mfaService.generateQrCodeImageUri(EncryptionUtils.decryptSecretKey(user.getMfaSecret())))
                .build();
    }

    public String disableMFA() {
        User user = findUserByMail(CommonUtils.getUserInfo()).orElseThrow();
        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        saveUser(user);
        return "MFA has been successfully disabled!";
    }
}
