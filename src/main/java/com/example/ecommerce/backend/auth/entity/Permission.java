package com.example.ecommerce.backend.auth.entity;

import com.example.ecommerce.backend.auth.enums.PermissionCode;
import com.example.ecommerce.backend.common.entity.Auditable;
import com.example.ecommerce.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Fine-grained authority used to protect business operations.
 *
 * <p>Permissions use the {@code PERMISSION_} prefix and are mapped directly to
 * Spring Security authorities distinct from {@code ROLE_} role authorities.</p>
 *
 * @author Pial Kanti Samadder
 */
@Entity
@Table(name = "permissions")
@Getter
@Setter
public class Permission extends BaseEntity implements Auditable {
    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    @Enumerated(EnumType.STRING)
    private PermissionCode code;

    @Column(length = 500)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "modified_by")
    private Long modifiedBy;
}
