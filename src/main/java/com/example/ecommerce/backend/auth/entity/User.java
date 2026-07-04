package com.example.ecommerce.backend.auth.entity;

import com.example.ecommerce.backend.common.entity.Auditable;
import com.example.ecommerce.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Application user account used for authentication.
 *
 * <p>The entity stores account identity and credential metadata while keeping
 * authorization concerns separate so role and permission tables can be added
 * without changing the core user model.</p>
 *
 * @author Pial Kanti Samadder
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity implements Auditable {
    /**
     * User's required first name.
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /**
     * User's optional last name.
     */
    @Column(name = "last_name", length = 100)
    private String lastName;

    /**
     * Unique username used during login.
     */
    @Column(nullable = false, length = 100, unique = true)
    private String username;

    /**
     * Unique email address associated with the account.
     */
    @Column(nullable = false, length = 150, unique = true)
    private String email;

    /**
     * BCrypt encoded password hash.
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Optional contact phone number.
     */
    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    /**
     * Indicates whether this account may authenticate.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Roles assigned to this user for authorization.
     */
    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Timestamp when the user account was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user account was last modified.
     */
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    /**
     * Identifier of the user that created this account.
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * Identifier of the user that last modified this account.
     */
    @Column(name = "modified_by")
    private Long modifiedBy;
}
