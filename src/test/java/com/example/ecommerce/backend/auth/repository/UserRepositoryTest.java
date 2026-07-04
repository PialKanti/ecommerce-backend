package com.example.ecommerce.backend.auth.repository;

import com.example.ecommerce.backend.auth.entity.Role;
import com.example.ecommerce.backend.auth.entity.User;
import com.example.ecommerce.backend.auth.enums.RoleCode;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link UserRepository} custom query methods.
 *
 * @author Pial Kanti Samadder
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    @DisplayName("Should return user with roles and permissions eagerly loaded for findByUsername")
    void shouldReturnUserWithEagerlyLoadedRolesAndPermissionsForFindByUsernameWhenUserHasRoles() {
        Role adminRole = findSeededRole(RoleCode.ADMIN);
        persistUserWithRole("jane", "jane@example.com", adminRole);
        entityManager.flush();
        entityManager.clear();

        Optional<User> result = userRepository.findByUsername("jane");

        assertThat(result).isPresent();
        User found = result.get();
        assertThat(found.getUsername()).isEqualTo("jane");
        assertThat(entityManagerFactory.getPersistenceUnitUtil().isLoaded(found, "roles")).isTrue();
        assertThat(found.getRoles()).hasSize(1);
        Role loadedRole = found.getRoles().iterator().next();
        assertThat(loadedRole.getCode()).isEqualTo(RoleCode.ADMIN);
        assertThat(entityManagerFactory.getPersistenceUnitUtil().isLoaded(loadedRole, "permissions")).isTrue();
        assertThat(loadedRole.getPermissions()).hasSize(25);
    }

    @Test
    @DisplayName("Should return user with empty roles collection for findByUsername when no roles are assigned")
    void shouldReturnUserWithEmptyRolesCollectionForFindByUsernameWhenNoRolesAssigned() {
        persistUser("bob", "bob@example.com");
        entityManager.flush();
        entityManager.clear();

        Optional<User> result = userRepository.findByUsername("bob");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("bob");
        assertThat(result.get().getRoles()).isEmpty();
    }

    @Test
    @DisplayName("Should return empty optional for findByUsername when username is not in the database")
    void shouldReturnEmptyOptionalForFindByUsernameWhenUsernameDoesNotExist() {
        Optional<User> result = userRepository.findByUsername("ghost");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return user with roles and permissions eagerly loaded for findWithRolesById")
    void shouldReturnUserWithEagerlyLoadedRolesAndPermissionsForFindWithRolesByIdWhenUserHasRoles() {
        Role adminRole = findSeededRole(RoleCode.ADMIN);
        User user = persistUserWithRole("alice", "alice@example.com", adminRole);
        Long userId = user.getId();
        entityManager.flush();
        entityManager.clear();

        Optional<User> result = userRepository.findWithRolesById(userId);

        assertThat(result).isPresent();
        User found = result.get();
        assertThat(found.getId()).isEqualTo(userId);
        assertThat(entityManagerFactory.getPersistenceUnitUtil().isLoaded(found, "roles")).isTrue();
        assertThat(found.getRoles()).hasSize(1);
        Role loadedRole = found.getRoles().iterator().next();
        assertThat(loadedRole.getCode()).isEqualTo(RoleCode.ADMIN);
        assertThat(entityManagerFactory.getPersistenceUnitUtil().isLoaded(loadedRole, "permissions")).isTrue();
        assertThat(loadedRole.getPermissions()).hasSize(25);
    }

    @Test
    @DisplayName("Should return empty optional for findWithRolesById when id does not exist")
    void shouldReturnEmptyOptionalForFindWithRolesByIdWhenIdDoesNotExist() {
        Optional<User> result = userRepository.findWithRolesById(Long.MAX_VALUE);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return true for existsByUsername when username exists in the database")
    void shouldReturnTrueForExistsByUsernameWhenUsernameExists() {
        persistUser("charlie", "charlie@example.com");
        entityManager.flush();
        entityManager.clear();

        assertThat(userRepository.existsByUsername("charlie")).isTrue();
    }

    @Test
    @DisplayName("Should return false for existsByUsername when username is not in the database")
    void shouldReturnFalseForExistsByUsernameWhenUsernameDoesNotExist() {
        assertThat(userRepository.existsByUsername("nobody")).isFalse();
    }

    @Test
    @DisplayName("Should return true for existsByEmail when email exists in the database")
    void shouldReturnTrueForExistsByEmailWhenEmailExists() {
        persistUser("diana", "diana@example.com");
        entityManager.flush();
        entityManager.clear();

        assertThat(userRepository.existsByEmail("diana@example.com")).isTrue();
    }

    @Test
    @DisplayName("Should return false for existsByEmail when email is not in the database")
    void shouldReturnFalseForExistsByEmailWhenEmailDoesNotExist() {
        assertThat(userRepository.existsByEmail("nobody@example.com")).isFalse();
    }

    private User persistUser(String username, String email) {
        return entityManager.persist(buildUser(username, email));
    }

    private User persistUserWithRole(String username, String email, Role role) {
        User user = buildUser(username, email);
        user.getRoles().add(role);
        return entityManager.persist(user);
    }

    private User buildUser(String username, String email) {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("$2a$10$hashedpasswordvalue00u");
        user.setIsActive(true);
        return user;
    }

    private Role findSeededRole(RoleCode code) {
        return entityManager.getEntityManager()
                .createQuery("select r from Role r where r.code = :code", Role.class)
                .setParameter("code", code)
                .getSingleResult();
    }
}
