package com.example.ecommerce.backend.product.entity;

import com.example.ecommerce.backend.common.entity.Auditable;
import com.example.ecommerce.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Product catalog item offered through the ecommerce system.
 *
 * <p>Each product is uniquely identified by its SKU and belongs to one
 * {@link Category}.</p>
 *
 * @author Pial Kanti Samadder
 */
@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends BaseEntity implements Auditable {
    /**
     * Unique stock keeping unit used as the product business identifier.
     */
    @Column(nullable = false, length = 100, unique = true)
    private String sku;

    /**
     * Human-readable product name displayed to users and administrators.
     */
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Optional detailed product description.
     */
    @Column(length = 1000)
    private String description;

    /**
     * Product selling price.
     */
    @Column(nullable = false)
    private Double price;

    /**
     * Indicates whether the product is available for active catalog use.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Category that owns this product.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * Optional URL or path for the product image.
     */
    @Column(name = "image_url", length = 250)
    private String imageUrl;

    /**
     * Timestamp when the product was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the product was last modified.
     */
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    /**
     * Identifier of the user that created the product.
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * Identifier of the user that last modified the product.
     */
    @Column(name = "modified_by")
    private Long modifiedBy;
}
