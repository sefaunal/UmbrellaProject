package com.sefaunal.umbrellachat.Service;

import com.sefaunal.umbrellachat.Request.AuthenticationRequest;
import com.sefaunal.umbrellachat.Request.RegisterRequest;
import com.sefaunal.umbrellachat.Response.AuthenticationResponse;
import com.sefaunal.umbrellachat.Model.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * @author github.com/sefaunal
 * @since 2023-09-18
 **/

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    private final AuthenticationManager authenticationManager;

    private final LoginHistoryService loginHistoryService;

    public AuthenticationResponse register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        userService.createUser(user);
        String JWT = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(JWT).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest servletRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userService.findUserByMail(request.getEmail()).orElseThrow();
        String JWT = jwtService.generateToken(user);
        CompletableFuture.runAsync(() -> loginHistoryService.saveLoginHistory(servletRequest, request.getEmail()));
        return AuthenticationResponse.builder().token(JWT).build();
    }
}
