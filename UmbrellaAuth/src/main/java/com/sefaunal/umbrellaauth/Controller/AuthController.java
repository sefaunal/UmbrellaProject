package com.sefaunal.umbrellaauth.Controller;

import com.sefaunal.umbrellaauth.Request.AuthenticationRequest;
import com.sefaunal.umbrellaauth.Request.RecoveryCodeRequest;
import com.sefaunal.umbrellaauth.Request.RegisterRequest;
import com.sefaunal.umbrellaauth.Request.VerificationRequest;
import com.sefaunal.umbrellaauth.Service.AuthService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author github.com/sefaunal
 * @since 2023-09-18
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthenticationRequest authenticationRequest,
                                          @Nonnull HttpServletRequest servletRequest,
                                          @Nonnull HttpSession httpSession) {
        return ResponseEntity.ok(authService.authenticate(authenticationRequest, servletRequest, httpSession));
    }

    @GetMapping("/oauth2/redirect/{provider}")
    public ResponseEntity<?> authenticate(@Nonnull HttpServletRequest servletRequest,
                                          @PathVariable String provider,
                                          @RequestParam String code) {
        return ResponseEntity.ok(authService.authenticate(servletRequest, provider, code));
    }

    @PostMapping("verify/totp")
    public ResponseEntity<?> verify2FACode(@Valid @RequestBody VerificationRequest verificationRequest,
                                           @Nonnull HttpServletRequest servletRequest,
                                           @Nonnull HttpSession httpSession) {
        return ResponseEntity.ok(authService.verifyTOTP(verificationRequest, servletRequest, httpSession));
    }

    @PostMapping("/verify/recoveryCode")
    public ResponseEntity<?> verifyRecoveryCode(@Valid @RequestBody RecoveryCodeRequest recoveryCodeRequest,
                                                @Nonnull HttpServletRequest servletRequest,
                                                @Nonnull HttpSession httpSession) {
        return ResponseEntity.ok(authService.verifyRecoveryCode(recoveryCodeRequest, servletRequest, httpSession));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response) {
        return ResponseEntity.ok(authService.logout(request, response));
    }
}
