package com.sefaunal.umbrellachat.Controller;

import com.sefaunal.umbrellachat.Service.BackupKeysService;
import com.sefaunal.umbrellachat.Service.UserService;
import com.sefaunal.umbrellachat.Util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/mfa/enable")
    public ResponseEntity<?> enableMFA() {
        return ResponseEntity.ok(userService.enableMFA());
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
