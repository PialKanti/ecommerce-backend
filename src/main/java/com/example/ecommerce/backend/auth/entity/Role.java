package com.example.ecommerce.backend.auth.entity;

import com.example.ecommerce.backend.auth.enums.RoleCode;
import com.example.ecommerce.backend.common.entity.Auditable;
import com.example.ecommerce.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Security role assigned to users and linked to operation permissions.
 *
 * <p>The {@code code} field stores enum-like values such as {@code ADMIN}
 * without the {@code ROLE_} prefix. Spring Security receives the prefix only
 * during authority mapping.</p>
 *
 * @author Pial Kanti Samadder
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role extends BaseEntity implements Auditable {
    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 80)
    @Enumerated(EnumType.STRING)
    private RoleCode code;

    @Column(length = 500)
    private String description;

    @ManyToMany
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "modified_by")
    private Long modifiedBy;
}
