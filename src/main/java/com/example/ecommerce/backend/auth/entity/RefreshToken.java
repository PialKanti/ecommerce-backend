package com.example.ecommerce.backend.auth.entity;

import com.example.ecommerce.backend.common.entity.Auditable;
import com.example.ecommerce.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Persisted refresh token record used for durable token rotation and revocation.
 *
 * <p>Only a SHA-256 hash of the refresh token is stored so the raw credential is
 * never persisted.</p>
 *
 * @author Pial Kanti Samadder
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
public class RefreshToken extends BaseEntity implements Auditable {
    /**
     * User that owns this refresh token.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * SHA-256 hash of the raw refresh token.
     */
    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    private String tokenHash;

    /**
     * Timestamp after which the refresh token cannot be used.
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Indicates whether this token has been explicitly revoked.
     */
    @Column(nullable = false)
    private Boolean revoked = false;

    /**
     * Timestamp when the refresh token was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the refresh token was last modified.
     */
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    /**
     * Identifier of the user that created this refresh token.
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * Identifier of the user that last modified this refresh token.
     */
    @Column(name = "modified_by")
    private Long modifiedBy;
}
