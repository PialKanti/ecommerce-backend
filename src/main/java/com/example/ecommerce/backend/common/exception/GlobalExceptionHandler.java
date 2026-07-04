package com.example.ecommerce.backend.common.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Handles common API exceptions and maps them to consistent response bodies.
 *
 * @author Pial Kanti Samadder
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles request validation errors.
     *
     * @param exception validation exception
     * @return bad request response with validation messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return problemDetail(HttpStatus.BAD_REQUEST, "Validation failed", message);
    }

    /**
     * Handles missing persistent resources.
     *
     * @param exception not found exception
     * @return not found response
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException exception) {
        return problemDetail(HttpStatus.NOT_FOUND, "Resource not found", exception.getMessage());
    }

    /**
     * Handles business conflicts such as duplicate resources or invalid stock transitions.
     *
     * @param exception conflict exception
     * @return conflict response
     */
    @ExceptionHandler(ResourceConflictException.class)
    public ProblemDetail handleResourceConflictException(ResourceConflictException exception) {
        return problemDetail(HttpStatus.CONFLICT, "Resource conflict", exception.getMessage());
    }

    /**
     * Handles authentication credential failures.
     *
     * @param exception authentication exception
     * @return unauthorized response
     */
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException exception) {
        return problemDetail(HttpStatus.UNAUTHORIZED, "Authentication failed", "Invalid username or password.");
    }

    /**
     * Handles authorization failures from method-level security checks.
     *
     * @param exception access denied exception
     * @return forbidden response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        return problemDetail(HttpStatus.FORBIDDEN, "Access denied", "You do not have permission to perform this action.");
    }

    /**
     * Handles invalid, expired, or revoked authentication tokens.
     *
     * @param exception invalid token exception
     * @return unauthorized response
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ProblemDetail handleInvalidTokenException(InvalidTokenException exception) {
        return problemDetail(HttpStatus.UNAUTHORIZED, "Invalid token", exception.getMessage());
    }

    /**
     * Handles optimistic locking conflicts from concurrent inventory updates.
     *
     * @param exception optimistic locking exception
     * @return conflict response
     */
    @ExceptionHandler({OptimisticLockingFailureException.class, OptimisticLockException.class})
    public ProblemDetail handleOptimisticLockingException(RuntimeException exception) {
        return problemDetail(
                HttpStatus.CONFLICT,
                "Concurrent update conflict",
                "Inventory was updated by another request. Please retry with the latest state.");
    }

    private ProblemDetail problemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        return problemDetail;
    }
}
