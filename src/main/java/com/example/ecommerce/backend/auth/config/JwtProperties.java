package com.example.ecommerce.backend.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration properties for JWT access and refresh token lifetimes.
 *
 * @author Pial Kanti Samadder
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String signingKey,
        Duration expiration,
        Duration refreshExpiration
) {
}
