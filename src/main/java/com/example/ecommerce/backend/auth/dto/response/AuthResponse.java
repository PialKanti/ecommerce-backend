package com.example.ecommerce.backend.auth.dto.response;

/**
 * Response payload returned after successful authentication or token refresh.
 *
 * @author Pial Kanti Samadder
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn
) {
}
