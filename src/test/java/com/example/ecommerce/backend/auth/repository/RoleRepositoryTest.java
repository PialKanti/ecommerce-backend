package com.example.ecommerce.backend.auth.repository;

import com.example.ecommerce.backend.auth.entity.Role;
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
 * Integration tests for {@link RoleRepository} custom query methods.
 *
 * @author Pial Kanti Samadder
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("RoleRepository Tests")
class RoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    @DisplayName("Should return true for existsByCode when role exists")
    void shouldReturnTrueForExistsByCodeWhenRoleExists() {
        assertThat(roleRepository.existsByCode(RoleCode.ADMIN)).isTrue();
    }

    @Test
    @DisplayName("Should return false for existsByCode after role is removed")
    void shouldReturnFalseForExistsByCodeAfterRoleIsRemoved() {
        entityManager.getEntityManager()
                .createQuery("delete from Role r where r.code = :code")
                .setParameter("code", RoleCode.CUSTOMER)
                .executeUpdate();
        entityManager.clear();

        assertThat(roleRepository.existsByCode(RoleCode.CUSTOMER)).isFalse();
    }

    @Test
    @DisplayName("Should return role with matching code for findByCode when role exists")
    void shouldReturnRoleWithMatchingCodeForFindByCodeWhenRoleExists() {
        Optional<Role> result = roleRepository.findByCode(RoleCode.ADMIN);

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo(RoleCode.ADMIN);
        assertThat(result.get().getName()).isEqualTo("Administrator");
    }

    @Test
    @DisplayName("Should return empty optional for findByCode after role is removed")
    void shouldReturnEmptyOptionalForFindByCodeAfterRoleIsRemoved() {
        entityManager.getEntityManager()
                .createQuery("delete from Role r where r.code = :code")
                .setParameter("code", RoleCode.CUSTOMER)
                .executeUpdate();
        entityManager.clear();

        Optional<Role> result = roleRepository.findByCode(RoleCode.CUSTOMER);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return role with all 25 permissions eagerly loaded for findWithPermissionsById")
    void shouldReturnRoleWithPermissionsEagerlyLoadedWhenRoleHasPermissions() {
        Long adminId = findSeededRole(RoleCode.ADMIN).getId();
        entityManager.clear();

        Optional<Role> result = roleRepository.findWithPermissionsById(adminId);

        assertThat(result).isPresent();
        Role found = result.get();
        assertThat(found.getCode()).isEqualTo(RoleCode.ADMIN);
        assertThat(entityManagerFactory.getPersistenceUnitUtil().isLoaded(found, "permissions")).isTrue();
        assertThat(found.getPermissions()).hasSize(25);
    }

    @Test
    @DisplayName("Should return role with empty permissions collection for findWithPermissionsById when role has no permissions")
    void shouldReturnEmptyPermissionsCollectionForFindWithPermissionsByIdWhenRoleHasNoPermissions() {
        Long customerId = findSeededRole(RoleCode.CUSTOMER).getId();
        entityManager.clear();

        Optional<Role> result = roleRepository.findWithPermissionsById(customerId);

        assertThat(result).isPresent();
        Role found = result.get();
        assertThat(found.getCode()).isEqualTo(RoleCode.CUSTOMER);
        assertThat(entityManagerFactory.getPersistenceUnitUtil().isLoaded(found, "permissions")).isTrue();
        assertThat(found.getPermissions()).isEmpty();
    }

    @Test
    @DisplayName("Should return empty optional for findWithPermissionsById when id does not exist")
    void shouldReturnEmptyOptionalForFindWithPermissionsByIdWhenIdDoesNotExist() {
        Optional<Role> result = roleRepository.findWithPermissionsById(Long.MAX_VALUE);

        assertThat(result).isEmpty();
    }

    private Role findSeededRole(RoleCode code) {
        return entityManager.getEntityManager()
                .createQuery("select r from Role r where r.code = :code", Role.class)
                .setParameter("code", code)
                .getSingleResult();
    }
}
