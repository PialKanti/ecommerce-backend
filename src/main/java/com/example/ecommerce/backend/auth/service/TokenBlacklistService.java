package com.example.ecommerce.backend.auth.service;

import java.time.Duration;

/**
 * Service interface for Redis-backed access token blacklisting.
 *
 * @author Pial Kanti Samadder
 */
public interface TokenBlacklistService {
    /**
     * Blacklists an access token until its expiration.
     *
     * @param token raw access token
     * @param ttl remaining token lifetime
     */
    void blacklist(String token, Duration ttl);

    /**
     * Checks whether an access token has been blacklisted.
     *
     * @param token raw access token
     * @return {@code true} when the token is blacklisted
     */
    boolean isBlacklisted(String token);
}
