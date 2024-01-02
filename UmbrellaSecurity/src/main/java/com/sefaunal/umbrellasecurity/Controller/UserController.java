package com.sefaunal.umbrellasecurity.Controller;

import com.sefaunal.umbrellasecurity.Request.VerificationRequest;
import com.sefaunal.umbrellasecurity.Service.BackupKeysService;
import com.sefaunal.umbrellasecurity.Service.UserService;
import com.sefaunal.umbrellasecurity.Util.CommonUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author github.com/sefaunal
 * @since 2023-12-15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    private final BackupKeysService keysService;

    @GetMapping("/mfa/generate")
    public ResponseEntity<?> generateSecretKey() {
        return ResponseEntity.ok(userService.generateSecretKey());
    }

    @GetMapping("/mfa/enable")
    public ResponseEntity<?> enableMFA(@Valid @RequestBody VerificationRequest verificationRequest) {
        return ResponseEntity.ok(userService.enableMFA(verificationRequest));
    }

    @GetMapping("/mfa/disable")
    public ResponseEntity<?> disableMFA() {
        return ResponseEntity.ok(userService.disableMFA());
    }

    @PostMapping("/mfa/recovery/generate")
    public ResponseEntity<?> generateRecoveryKeys() {
        return ResponseEntity.ok(keysService.generateRecoveryKeys());
    }

    @PostMapping("/mfa/recovery/view")
    public ResponseEntity<?> viewRecoveryKeys() {
        return ResponseEntity.ok(keysService.decryptRecoveryKeys(CommonUtils.getUserInfo()));
    }
}
