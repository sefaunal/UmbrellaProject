package com.sefaunal.umbrellachat.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.sefaunal.umbrellachat.Config.RsaKeyProperties;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * @author github.com/sefaunal
 * @since 2023-09-17
 **/

@Service
@AllArgsConstructor
public class JWTService {

    private final RsaKeyProperties rsaKeys;

    public String generateToken(Authentication authentication) {
        String userID = ((UserDetails) authentication.getPrincipal()).getUsername();

        return JWT.create()
                .withSubject(userID)
                .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 1000))
                .withIssuer("Umbrella Corp.")
                .withIssuedAt(Instant.now())
                .sign(Algorithm.RSA256(rsaKeys.rsaPublicKey(), rsaKeys.rsaPrivateKey()));
    }

    public String generateToken(UserDetails userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 1000))
                .withIssuer("Umbrella Corp.")
                .withIssuedAt(Instant.now())
                .sign(Algorithm.RSA256(rsaKeys.rsaPublicKey(), rsaKeys.rsaPrivateKey()));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token) {
        return decodeToken(token).getSubject();
    }

    private Date extractExpiration(String token) {
        return decodeToken(token).getExpiresAt();
    }

    private DecodedJWT decodeToken(String token) {
        RSAKeyProvider keyProvider = new RSAKeyProvider() {
            @Override
            public RSAPublicKey getPublicKeyById(String keyId) {
                return rsaKeys.rsaPublicKey();
            }

            @Override
            public RSAPrivateKey getPrivateKey() {
                return rsaKeys.rsaPrivateKey();
            }

            @Override
            public String getPrivateKeyId() {
                return null;
            }
        };

        Algorithm algorithm = Algorithm.RSA256(keyProvider);

        return JWT
                .require(algorithm)
                .build()
                .verify(token);
    }
}
