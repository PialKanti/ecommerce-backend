package com.example.ecommerce.backend.auth.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;

/**
 * Service interface for JWT creation and validation operations.
 *
 * @author Pial Kanti Samadder
 */
public interface JwtService {
    /**
     * Generates a signed access token for the supplied user.
     *
     * @param userDetails authenticated user details
     * @return signed JWT access token
     */
    String generateAccessToken(UserDetails userDetails);

    /**
     * Extracts the username subject from a JWT.
     *
     * @param token JWT value
     * @return username subject
     */
    String extractUsername(String token);

    /**
     * Validates a token against user details, signature, and expiration.
     *
     * @param token JWT value
     * @param userDetails expected user details
     * @return {@code true} when the token is valid
     */
    boolean isTokenValid(String token, UserDetails userDetails);

    /**
     * Calculates the remaining lifetime of a token.
     *
     * @param token JWT value
     * @return remaining token lifetime
     */
    Duration getRemainingLifetime(String token);

    /**
     * Returns the configured access token expiration.
     *
     * @return access token lifetime
     */
    Duration getAccessTokenExpiration();
}
