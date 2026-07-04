package com.example.ecommerce.backend.auth.service.impl;

import com.example.ecommerce.backend.auth.config.JwtProperties;
import com.example.ecommerce.backend.auth.dto.request.LoginRequest;
import com.example.ecommerce.backend.auth.dto.request.RefreshTokenRequest;
import com.example.ecommerce.backend.auth.dto.request.RegisterRequest;
import com.example.ecommerce.backend.auth.dto.response.AuthResponse;
import com.example.ecommerce.backend.auth.dto.response.UserResponse;
import com.example.ecommerce.backend.auth.entity.Role;
import com.example.ecommerce.backend.auth.entity.RefreshToken;
import com.example.ecommerce.backend.auth.entity.User;
import com.example.ecommerce.backend.auth.enums.RoleCode;
import com.example.ecommerce.backend.common.exception.InvalidTokenException;
import com.example.ecommerce.backend.auth.mapper.UserMapper;
import com.example.ecommerce.backend.auth.repository.RefreshTokenRepository;
import com.example.ecommerce.backend.auth.repository.RoleRepository;
import com.example.ecommerce.backend.auth.repository.UserRepository;
import com.example.ecommerce.backend.auth.util.TokenHashUtil;
import com.example.ecommerce.backend.auth.service.AuthService;
import com.example.ecommerce.backend.auth.service.JwtService;
import com.example.ecommerce.backend.auth.service.TokenBlacklistService;
import com.example.ecommerce.backend.common.exception.ResourceConflictException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Default implementation of authentication, registration, and token workflows.
 *
 * @author Pial Kanti Samadder
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final String TOKEN_TYPE = "Bearer";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int REFRESH_TOKEN_BYTES = 64;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ResourceConflictException("User with username '" + request.username() + "' already exists.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceConflictException("User with email '" + request.email() + "' already exists.");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setIsActive(Boolean.TRUE);
        user.getRoles().add(defaultCustomerRole());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.username()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = createRefreshToken(user);
        return authResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = getValidRefreshToken(request.refreshToken());
        refreshToken.setRevoked(Boolean.TRUE);
        refreshTokenRepository.save(refreshToken);

        User user = refreshToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String accessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = createRefreshToken(user);
        return authResponse(accessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void logout(String bearerToken, RefreshTokenRequest request) {
        String accessToken = extractBearerToken(bearerToken);
        Duration ttl;
        try {
            ttl = jwtService.getRemainingLifetime(accessToken);
        } catch (RuntimeException exception) {
            throw new InvalidTokenException("Access token is invalid.");
        }
        tokenBlacklistService.blacklist(accessToken, ttl);

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(TokenHashUtil.sha256(request.refreshToken()))
                .orElseThrow(() -> new InvalidTokenException("Refresh token is invalid."));
        refreshToken.setRevoked(Boolean.TRUE);
        refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken getValidRefreshToken(String rawToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(TokenHashUtil.sha256(rawToken))
                .orElseThrow(() -> new InvalidTokenException("Refresh token is invalid."));

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw new InvalidTokenException("Refresh token has been revoked.");
        }
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshToken.setRevoked(Boolean.TRUE);
            refreshTokenRepository.save(refreshToken);
            throw new InvalidTokenException("Refresh token has expired.");
        }
        if (!Boolean.TRUE.equals(refreshToken.getUser().getIsActive())) {
            throw new InvalidTokenException("User account is inactive.");
        }

        return refreshToken;
    }

    private Role defaultCustomerRole() {
        return roleRepository.findByCode(RoleCode.CUSTOMER)
                .orElseThrow(() -> new EntityNotFoundException("Default role not found: " + RoleCode.CUSTOMER.name()));
    }

    private String createRefreshToken(User user) {
        String rawToken = generateSecureToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(TokenHashUtil.sha256(rawToken));
        refreshToken.setExpiresAt(LocalDateTime.now().plus(jwtProperties.refreshExpiration()));
        refreshToken.setRevoked(Boolean.FALSE);
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    private AuthResponse authResponse(String accessToken, String refreshToken) {
        return new AuthResponse(
                accessToken,
                refreshToken,
                TOKEN_TYPE,
                jwtService.getAccessTokenExpiration().toSeconds());
    }

    private String generateSecureToken() {
        byte[] tokenBytes = new byte[REFRESH_TOKEN_BYTES];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private String extractBearerToken(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith(BEARER_PREFIX)) {
            throw new InvalidTokenException("Bearer access token is required.");
        }
        return bearerToken.substring(BEARER_PREFIX.length());
    }
}
