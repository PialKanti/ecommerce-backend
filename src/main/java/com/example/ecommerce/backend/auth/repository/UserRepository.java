package com.example.ecommerce.backend.auth.repository;

import com.example.ecommerce.backend.auth.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for user account persistence operations.
 *
 * @author Pial Kanti Samadder
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by username.
     *
     * @param username unique account username
     * @return matching user when present
     */
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by identifier with assigned roles loaded.
     *
     * @param id user identifier
     * @return matching user when present
     */
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    @Query("select user from User user where user.id = :id")
    Optional<User> findWithRolesById(@Param("id") Long id);

    /**
     * Checks whether a username is already used.
     *
     * @param username candidate username
     * @return {@code true} when the username exists
     */
    boolean existsByUsername(String username);

    /**
     * Checks whether an email address is already used.
     *
     * @param email candidate email address
     * @return {@code true} when the email exists
     */
    boolean existsByEmail(String email);
}
