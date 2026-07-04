package com.example.ecommerce.backend.auth.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility for hashing sensitive token values before persistence or cache storage.
 *
 * @author Pial Kanti Samadder
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenHashUtil {
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    /**
     * Returns a lowercase SHA-256 hex hash for the supplied token.
     *
     * @param token raw token value
     * @return SHA-256 hex hash
     */
    public static String sha256(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            char[] hexChars = new char[hash.length * 2];
            for (int i = 0; i < hash.length; i++) {
                int value = hash[i] & 0xFF;
                hexChars[i * 2] = HEX_ARRAY[value >>> 4];
                hexChars[i * 2 + 1] = HEX_ARRAY[value & 0x0F];
            }
            return new String(hexChars);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", exception);
        }
    }
}
