package com.example.ecommerce.backend.order.entity;

import com.example.ecommerce.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Immutable product snapshot stored as a line item within an order.
 *
 * <p>Only product identifiers and display/pricing snapshots are stored so
 * historical orders remain stable after product catalog edits.</p>
 *
 * @author Pial Kanti Samadder
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseEntity {
    /**
     * Owning order.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Product identifier captured at checkout time.
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * Product name captured at checkout time.
     */
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    /**
     * Unit price captured at checkout time.
     */
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    /**
     * Quantity purchased.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Quantity multiplied by the unit price snapshot.
     */
    @Column(name = "total_price", nullable = false)
    private Double totalPrice;
}
