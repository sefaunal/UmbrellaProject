package com.sefaunal.umbrellachat.Controller;

import com.sefaunal.umbrellachat.Request.AuthenticationRequest;
import com.sefaunal.umbrellachat.Request.RegisterRequest;
import com.sefaunal.umbrellachat.Request.VerificationRequest;
import com.sefaunal.umbrellachat.Service.AuthService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author github.com/sefaunal
 * @since 2023-09-18
 **/

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

    @PostMapping("verify")
    public ResponseEntity<?> verify2FACode(@Valid @RequestBody VerificationRequest verificationRequest,
                                           @Nonnull HttpServletRequest servletRequest,
                                           @Nonnull HttpSession httpSession) {
        return ResponseEntity.ok(authService.verifyMFA(verificationRequest, servletRequest, httpSession));
    }
}
