package com.sefaunal.umbrellasecurity.Service;

import com.sefaunal.umbrellasecurity.Model.BackupKeys;
import com.sefaunal.umbrellasecurity.Request.AuthenticationRequest;
import com.sefaunal.umbrellasecurity.Request.RecoveryCodeRequest;
import com.sefaunal.umbrellasecurity.Request.RegisterRequest;
import com.sefaunal.umbrellasecurity.Request.VerificationRequest;
import com.sefaunal.umbrellasecurity.Response.AuthenticationResponse;
import com.sefaunal.umbrellasecurity.Model.User;
import com.sefaunal.umbrellasecurity.Response.GenericResponse;
import com.sefaunal.umbrellasecurity.Util.EncryptionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.List;
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

    private final BackupKeysService backupKeysService;

    private final OAuth2Service oAuth2Service;

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
        return AuthenticationResponse.builder().token(JWT).mfaEnabled(false).build();
    }

    public AuthenticationResponse authenticate(HttpServletRequest servletRequest, String provider, String token) {
        return oAuth2Service.authenticateOAuth2(servletRequest, provider, token);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest servletRequest, HttpSession httpSession) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userService.findUserByMail(request.getEmail());

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

    public AuthenticationResponse verifyTOTP(VerificationRequest request, HttpServletRequest servletRequest, HttpSession httpSession) {
        String userMail = (String) httpSession.getAttribute("authenticatedUser");
        if (userMail == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        User user = userService.findUserByMail(userMail);

        if (!mfaService.isOtpValid(EncryptionUtils.decryptSecretKey(user.getMfaSecret()), request.getMfaCode())) {
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

    public AuthenticationResponse verifyRecoveryCode(RecoveryCodeRequest recoveryCodeRequest,
                                                     HttpServletRequest servletRequest,
                                                     HttpSession httpSession) {
        String userMail = (String) httpSession.getAttribute("authenticatedUser");
        if (userMail == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        User user = userService.findUserByMail(userMail);

        BackupKeys backupKeys = backupKeysService.obtainEncryptedRecoveryCodes(userMail);
        List<String> decryptedBackupKeys = backupKeysService.decryptRecoveryKeys(userMail).getRecoveryCodes();

        boolean userAuthenticated = false;
        for (int i = 0; i < decryptedBackupKeys.size(); i++) {
            if (recoveryCodeRequest.getRecoveryCode().equals(decryptedBackupKeys.get(i))) {
                backupKeys.getRecoveryCodes().remove(i);
                userAuthenticated = true;
                break;
            }
        }

        if (!userAuthenticated) {
            throw new BadCredentialsException("Recovery Code is not valid!");
        }

        backupKeysService.updateRecoveryCodesState(backupKeys);

        httpSession.invalidate();
        String JWT = jwtService.generateToken(user);
        CompletableFuture.runAsync(() -> loginHistoryService.saveLoginHistory(servletRequest, userMail));
        return AuthenticationResponse.builder()
                .token(JWT)
                .mfaEnabled(user.isMfaEnabled())
                .build();
    }

    public GenericResponse logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return new GenericResponse(200, "Successfully Logged out.");
    }

}
