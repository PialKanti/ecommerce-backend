package com.example.ecommerce.backend.auth.service;

import com.example.ecommerce.backend.auth.dto.request.LoginRequest;
import com.example.ecommerce.backend.auth.dto.request.RefreshTokenRequest;
import com.example.ecommerce.backend.auth.dto.request.RegisterRequest;
import com.example.ecommerce.backend.auth.dto.response.AuthResponse;
import com.example.ecommerce.backend.auth.dto.response.UserResponse;

/**
 * Service interface for authentication and account registration workflows.
 *
 * @author Pial Kanti Samadder
 */
public interface AuthService {
    /**
     * Registers a new user account.
     *
     * @param request registration details
     * @return safe user response
     */
    UserResponse register(RegisterRequest request);

    /**
     * Authenticates a user and issues tokens.
     *
     * @param request login credentials
     * @return authentication token response
     */
    AuthResponse login(LoginRequest request);

    /**
     * Rotates a valid refresh token and issues a new access token.
     *
     * @param request refresh token payload
     * @return authentication token response
     */
    AuthResponse refresh(RefreshTokenRequest request);

    /**
     * Revokes the supplied refresh token and blacklists the active access token.
     *
     * @param bearerToken authorization header containing the active access token
     * @param request refresh token payload
     */
    void logout(String bearerToken, RefreshTokenRequest request);
}
