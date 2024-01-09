package com.sefaunal.umbrellaauth.Service;

import com.sefaunal.umbrellaauth.Model.BackupKeys;
import com.sefaunal.umbrellaauth.Model.User;
import com.sefaunal.umbrellaauth.Repository.BackupKeysRepository;
import com.sefaunal.umbrellaauth.Response.RecoveryCodesResponse;
import com.sefaunal.umbrellaauth.Util.CommonUtils;
import com.sefaunal.umbrellaauth.Util.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author github.com/sefaunal
 * @since 2023-12-18
 */
@Service
@RequiredArgsConstructor
public class BackupKeysService {
    private final BackupKeysRepository backupKeysRepository;

    private final UserService userService;

    private final MFAService mfaService;

    public List<String> generateRecoveryKeys() {
        List<String> encryptedRecoveryCodes = EncryptionUtils.encryptRecoveryCodes(mfaService.generateRecoveryCodes());
        User user = userService.findUserByMail(CommonUtils.getUserInfo());

        BackupKeys backupKeys = new BackupKeys(encryptedRecoveryCodes, user.getID());

        backupKeysRepository.save(backupKeys);

        return null;
    }

    public RecoveryCodesResponse decryptRecoveryKeys(String userMail) {
        User user = userService.findUserByMail(userMail);
        BackupKeys backupKeys = backupKeysRepository.findByUserID(user.getID());

        List<String> decryptedBackupKeys = EncryptionUtils.decryptRecoveryCodes(backupKeys.getRecoveryCodes());

        return RecoveryCodesResponse
                .builder()
                .recoveryCodes(decryptedBackupKeys)
                .build();
    }

    public BackupKeys obtainEncryptedRecoveryCodes(String userMail) {
        User user = userService.findUserByMail(userMail);
        return backupKeysRepository.findByUserID(user.getID());
    }

    public void updateRecoveryCodesState(BackupKeys backupKeys) {
        backupKeysRepository.save(backupKeys);
    }
}
