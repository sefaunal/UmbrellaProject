package com.sefaunal.umbrellachat.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.sefaunal.umbrellachat.Config.RSA256Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;

/**
 * @author github.com/sefaunal
 * @since 2023-09-17
 */
@Service
public class JWTService {
    private final RSA256Keys rsaKeys;

    @Value("${umbrella.variables.jwt.expire.days}")
    private Integer JWTExpireDuration;

    public JWTService(RSA256Keys rsaKeys) {
        this.rsaKeys = rsaKeys;
    }

    public String generateToken(UserDetails userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JWTExpireDuration * 24 * 60 * 1000))
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
