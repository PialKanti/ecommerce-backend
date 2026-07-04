package com.example.ecommerce.backend.auth.service.impl;

import com.example.ecommerce.backend.auth.service.TokenBlacklistService;
import com.example.ecommerce.backend.auth.util.TokenHashUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

/**
 * In-memory implementation of access token blacklisting.
 *
 * @author Pial Kanti Samadder
 */
@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final Cache<String, Instant> blacklist = Caffeine.newBuilder()
            .expireAfter(new Expiry<String, Instant>() {
                @Override
                public long expireAfterCreate(String key, Instant expiresAt, long currentTime) {
                    return Math.max(0, Duration.between(Instant.now(), expiresAt).toNanos());
                }

                @Override
                public long expireAfterUpdate(String key, Instant expiresAt, long currentTime, long currentDuration) {
                    return expireAfterCreate(key, expiresAt, currentTime);
                }

                @Override
                public long expireAfterRead(String key, Instant expiresAt, long currentTime, long currentDuration) {
                    return currentDuration;
                }
            })
            .build();

    @Override
    public void blacklist(String token, Duration ttl) {
        if (ttl.isZero() || ttl.isNegative()) {
            return;
        }
        blacklist.put(buildKey(token), Instant.now().plus(ttl));
    }

    @Override
    public boolean isBlacklisted(String token) {
        return blacklist.getIfPresent(buildKey(token)) != null;
    }

    private String buildKey(String token) {
        return TokenHashUtil.sha256(token);
    }
}
