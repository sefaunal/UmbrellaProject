package com.sefaunal.umbrellachat.Service;

import com.sefaunal.umbrellachat.Request.AuthenticationRequest;
import com.sefaunal.umbrellachat.Request.RegisterRequest;
import com.sefaunal.umbrellachat.Request.VerificationRequest;
import com.sefaunal.umbrellachat.Response.AuthenticationResponse;
import com.sefaunal.umbrellachat.Model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * @author github.com/sefaunal
 * @since 2023-09-18
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    private final AuthenticationManager authenticationManager;

    private final LoginHistoryService loginHistoryService;

    private final MFAService mfaService;

    public AuthenticationResponse register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        userService.saveUser(user);
        String JWT = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(JWT).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest servletRequest, HttpSession httpSession) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userService.findUserByMail(request.getEmail()).orElseThrow();

        if (user.isMfaEnabled()) {
            httpSession.setAttribute("authenticatedUser", request.getEmail());
            return AuthenticationResponse.builder()
                    .mfaEnabled(true)
                    .build();
        }

        String JWT = jwtService.generateToken(user);
        CompletableFuture.runAsync(() -> loginHistoryService.saveLoginHistory(servletRequest, request.getEmail()));
        return AuthenticationResponse.builder()
                .token(JWT)
                .mfaEnabled(false)
                .build();
    }

    public AuthenticationResponse verifyMFA(VerificationRequest request, HttpServletRequest servletRequest, HttpSession httpSession) {
        String userMail = (String) httpSession.getAttribute("authenticatedUser");
        if (userMail == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        User user = userService.findUserByMail(userMail)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with " + userMail));

        if (!mfaService.isOtpValid(user.getMfaSecret(), request.getMfaCode())) {
            throw new BadCredentialsException("MFA Code is not valid!");
        }

        httpSession.invalidate();
        String JWT = jwtService.generateToken(user);
        CompletableFuture.runAsync(() -> loginHistoryService.saveLoginHistory(servletRequest, userMail));
        return AuthenticationResponse.builder()
                .token(JWT)
                .mfaEnabled(user.isMfaEnabled())
                .build();
    }
}
