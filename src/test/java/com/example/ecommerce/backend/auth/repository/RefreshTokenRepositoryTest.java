package com.example.ecommerce.backend.auth.repository;

import com.example.ecommerce.backend.auth.entity.RefreshToken;
import com.example.ecommerce.backend.auth.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link RefreshTokenRepository} custom query methods.
 *
 * @author Pial Kanti Samadder
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("RefreshTokenRepository Tests")
class RefreshTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("Should return refresh token for findByTokenHash when hash matches a persisted record")
    void shouldReturnRefreshTokenForFindByTokenHashWhenHashMatches() {
        User user = persistUser("frank", "frank@example.com");
        String tokenHash = "a".repeat(64);
        persistRefreshToken(user, tokenHash);
        entityManager.flush();
        entityManager.clear();

        Optional<RefreshToken> result = refreshTokenRepository.findByTokenHash(tokenHash);

        assertThat(result).isPresent();
        assertThat(result.get().getTokenHash()).isEqualTo(tokenHash);
        assertThat(result.get().getRevoked()).isFalse();
        assertThat(result.get().getExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should return empty optional for findByTokenHash when hash is not in the database")
    void shouldReturnEmptyOptionalForFindByTokenHashWhenHashIsNotFound() {
        Optional<RefreshToken> result = refreshTokenRepository.findByTokenHash("0".repeat(64));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return correct token for findByTokenHash when multiple tokens exist for the same user")
    void shouldReturnCorrectTokenForFindByTokenHashWhenMultipleTokensExistForSameUser() {
        User user = persistUser("grace", "grace@example.com");
        String firstHash = "1".repeat(64);
        String secondHash = "2".repeat(64);
        RefreshToken firstToken = persistRefreshToken(user, firstHash);
        RefreshToken secondToken = persistRefreshToken(user, secondHash);
        entityManager.flush();
        entityManager.clear();

        Optional<RefreshToken> resultForFirst = refreshTokenRepository.findByTokenHash(firstHash);
        Optional<RefreshToken> resultForSecond = refreshTokenRepository.findByTokenHash(secondHash);

        assertThat(resultForFirst).isPresent();
        assertThat(resultForFirst.get().getId()).isEqualTo(firstToken.getId());
        assertThat(resultForFirst.get().getTokenHash()).isEqualTo(firstHash);

        assertThat(resultForSecond).isPresent();
        assertThat(resultForSecond.get().getId()).isEqualTo(secondToken.getId());
        assertThat(resultForSecond.get().getTokenHash()).isEqualTo(secondHash);
    }

    private User persistUser(String username, String email) {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("$2a$10$hashedpasswordvalue00u");
        user.setIsActive(true);
        return entityManager.persist(user);
    }

    private RefreshToken persistRefreshToken(User user, String tokenHash) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenHash(tokenHash);
        token.setExpiresAt(LocalDateTime.now().plusDays(7));
        token.setRevoked(false);
        return entityManager.persist(token);
    }
}
