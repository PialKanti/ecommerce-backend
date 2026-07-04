package com.example.ecommerce.backend.inventory.entity;

import com.example.ecommerce.backend.common.entity.Auditable;
import com.example.ecommerce.backend.common.entity.BaseEntity;
import com.example.ecommerce.backend.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Product inventory record used to track stock and reservations.
 *
 * <p>Each inventory row belongs to exactly one {@link Product}. Total quantity
 * represents the stock held by the system, while reserved quantity represents
 * stock temporarily allocated to in-progress order workflows. Available stock is
 * derived by subtracting reserved quantity from total quantity.</p>
 *
 * <p>The {@link Version} field enables optimistic locking so concurrent stock
 * mutations cannot silently overwrite each other.</p>
 *
 * @author Pial Kanti Samadder
 */
@Entity
@Table(name = "inventories")
@Getter
@Setter
public class Inventory extends BaseEntity implements Auditable {
    /**
     * Product whose stock is represented by this inventory record.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Total stock quantity currently held for the product.
     */
    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity = 0;

    /**
     * Quantity currently reserved from total stock for pending workflows.
     */
    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    /**
     * Persistence-managed version used for optimistic locking.
     */
    @Version
    private Long version;

    /**
     * Timestamp when the inventory record was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the inventory record was last modified.
     */
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    /**
     * Identifier of the user that created the inventory record.
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * Identifier of the user that last modified the inventory record.
     */
    @Column(name = "modified_by")
    private Long modifiedBy;
}
