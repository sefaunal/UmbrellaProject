package com.sefaunal.umbrellachat.Controller;

import com.sefaunal.umbrellachat.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sefaunal
 * @since 2023-12-15
 */

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/mfa/enable")
    public ResponseEntity<?> enableMFA() {
        return ResponseEntity.ok(userService.enableMFA());
    }

    @GetMapping("/mfa/disable")
    public ResponseEntity<?> disableMFA() {
        return ResponseEntity.ok(userService.disableMFA());
    }
}
