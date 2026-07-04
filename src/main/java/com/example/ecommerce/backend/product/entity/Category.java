package com.example.ecommerce.backend.product.entity;

import com.example.ecommerce.backend.common.entity.Auditable;
import com.example.ecommerce.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Product catalog category used to group related products.
 *
 * <p>Category codes are unique and provide a stable business identifier for
 * lookup, integration, and administration workflows.</p>
 *
 * @author Pial Kanti Samadder
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category extends BaseEntity implements Auditable {
    /**
     * Human-readable category name displayed in catalog and admin workflows.
     */
    @Column(nullable = false, length = 120)
    private String name;

    /**
     * Unique business code for the category.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /**
     * Short description of the category's catalog scope.
     */
    @Column(length = 500)
    private String description;

    /**
     * Indicates whether the category is available for active catalog use.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Timestamp when the category was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the category was last modified.
     */
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    /**
     * Identifier of the user that created the category.
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * Identifier of the user that last modified the category.
     */
    @Column(name = "modified_by")
    private Long modifiedBy;
}
