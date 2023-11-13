package com.sefaunal.umbrellachat.Service;

import com.sefaunal.umbrellachat.Repository.UserRepository;
import com.sefaunal.umbrellachat.Request.AuthenticationRequest;
import com.sefaunal.umbrellachat.Request.RegisterRequest;
import com.sefaunal.umbrellachat.Response.AuthenticationResponse;
import com.sefaunal.umbrellachat.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author github.com/sefaunal
 * created on 2023-09-18
 **/

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        userRepository.save(user);
        String JWT = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(JWT).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail());
        String JWT = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(JWT).build();
    }
}
