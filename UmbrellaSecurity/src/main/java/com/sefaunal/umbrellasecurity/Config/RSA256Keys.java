package com.sefaunal.umbrellasecurity.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author github.com/sefaunal
 * @since 2023-09-17
 */
@ConfigurationProperties(prefix = "rsa")
public record RSA256Keys(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {}