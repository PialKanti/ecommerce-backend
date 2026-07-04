package com.example.ecommerce.backend.order.entity;

import com.example.ecommerce.backend.common.entity.Auditable;
import com.example.ecommerce.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Customer order created from a confirmed cart checkout.
 *
 * <p>The order stores immutable line-item snapshots so later product catalog
 * changes do not affect historical order details.</p>
 *
 * @author Pial Kanti Samadder
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(
        name = "Order.withItems",
        attributeNodes = @NamedAttributeNode("items")
)
public class Order extends BaseEntity implements Auditable {
    /**
     * Public order identifier used by clients and support workflows.
     */
    @Column(name = "order_number", nullable = false, unique = true, updatable = false)
    private UUID orderNumber;

    /**
     * Owner of the order.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Current order lifecycle state.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    /**
     * Sum of all order item total prices.
     */
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    /**
     * Timestamp set when the order is cancelled.
     */
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    /**
     * Snapshot line items attached to this order.
     */
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Timestamp when the order was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the order was last modified.
     */
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    /**
     * Identifier of the user that created the order.
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * Identifier of the user that last modified the order.
     */
    @Column(name = "modified_by")
    private Long modifiedBy;
}
