package com.example.ecommerce.backend.common.exception;

/**
 * Exception thrown when an authentication token is missing, invalid, expired, or revoked.
 *
 * @author Pial Kanti Samadder
 */
public class InvalidTokenException extends RuntimeException {
    /**
     * Creates an invalid token exception with a message.
     *
     * @param message exception message
     */
    public InvalidTokenException(String message) {
        super(message);
    }
}
