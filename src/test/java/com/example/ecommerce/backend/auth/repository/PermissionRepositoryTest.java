package com.example.ecommerce.backend.auth.repository;

import com.example.ecommerce.backend.auth.entity.Permission;
import com.example.ecommerce.backend.auth.enums.PermissionCode;
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
 * Integration tests for {@link PermissionRepository} custom query methods.
 *
 * @author Pial Kanti Samadder
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("PermissionRepository Tests")
class PermissionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    @DisplayName("Should return true for existsByCode when permission exists")
    void shouldReturnTrueForExistsByCodeWhenPermissionExists() {
        assertThat(permissionRepository.existsByCode(PermissionCode.PERMISSION_PRODUCT_READ)).isTrue();
    }

    @Test
    @DisplayName("Should return false for existsByCode after permission is removed")
    void shouldReturnFalseForExistsByCodeAfterPermissionIsRemoved() {
        entityManager.getEntityManager()
                .createQuery("delete from Permission p where p.code = :code")
                .setParameter("code", PermissionCode.PERMISSION_PRODUCT_READ)
                .executeUpdate();
        entityManager.clear();

        assertThat(permissionRepository.existsByCode(PermissionCode.PERMISSION_PRODUCT_READ)).isFalse();
    }

    @Test
    @DisplayName("Should return matching permission for findByCode when permission exists")
    void shouldReturnMatchingPermissionForFindByCodeWhenPermissionExists() {
        Optional<Permission> result = permissionRepository.findByCode(PermissionCode.PERMISSION_PRODUCT_READ);

        assertThat(result).isPresent();
        Permission found = result.get();
        assertThat(found.getCode()).isEqualTo(PermissionCode.PERMISSION_PRODUCT_READ);
        assertThat(found.getName()).isEqualTo("Read Products");
        assertThat(found.getDescription()).isNotBlank();
    }

    @Test
    @DisplayName("Should return empty optional for findByCode after permission is removed")
    void shouldReturnEmptyOptionalForFindByCodeAfterPermissionIsRemoved() {
        entityManager.getEntityManager()
                .createQuery("delete from Permission p where p.code = :code")
                .setParameter("code", PermissionCode.PERMISSION_CATEGORY_READ)
                .executeUpdate();
        entityManager.clear();

        Optional<Permission> result = permissionRepository.findByCode(PermissionCode.PERMISSION_CATEGORY_READ);

        assertThat(result).isEmpty();
    }
}
