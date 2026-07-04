package com.example.ecommerce.backend.auth.repository;

import com.example.ecommerce.backend.auth.entity.Role;
import com.example.ecommerce.backend.auth.enums.RoleCode;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for role persistence and lookup operations.
 *
 * @author Pial Kanti Samadder
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByCode(RoleCode code);

    Optional<Role> findByCode(RoleCode code);

    @EntityGraph(attributePaths = "permissions")
    @Query("select role from Role role where role.id = :id")
    Optional<Role> findWithPermissionsById(@Param("id") Long id);
}
