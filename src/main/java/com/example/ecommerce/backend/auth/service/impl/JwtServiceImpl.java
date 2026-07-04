package com.example.ecommerce.backend.auth.service.impl;

import com.example.ecommerce.backend.auth.config.JwtProperties;
import com.example.ecommerce.backend.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Date;
import java.util.function.Function;

/**
 * JJWT-based implementation of access token operations.
 *
 * @author Pial Kanti Samadder
 */
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String ROLES_CLAIM = "roles";
    private static final String PERMISSIONS_CLAIM = "permissions";

    private final JwtProperties jwtProperties;

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim(ROLES_CLAIM, extractRoles(userDetails))
                .claim(PERMISSIONS_CLAIM, extractPermissions(userDetails))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtProperties.expiration())))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (JwtException | IllegalArgumentException exception) {
            return null;
        }
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username != null
                && username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    @Override
    public Duration getRemainingLifetime(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        long remainingMillis = expiration.toInstant().toEpochMilli() - Instant.now().toEpochMilli();
        return Duration.ofMillis(Math.max(remainingMillis, 0));
    }

    @Override
    public Duration getAccessTokenExpiration() {
        return jwtProperties.expiration();
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            return true;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.signingKey().getBytes(StandardCharsets.UTF_8));
    }

    private List<String> extractRoles(UserDetails userDetails) {
        return userDetails.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> authority.startsWith(ROLE_PREFIX))
                .map(authority -> authority.substring(ROLE_PREFIX.length()))
                .sorted()
                .toList();
    }

    private List<String> extractPermissions(UserDetails userDetails) {
        return userDetails.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> !authority.startsWith(ROLE_PREFIX))
                .sorted()
                .toList();
    }
}
