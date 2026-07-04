package com.example.ecommerce.backend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload containing a refresh token credential.
 *
 * @author Pial Kanti Samadder
 */
public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {
}
