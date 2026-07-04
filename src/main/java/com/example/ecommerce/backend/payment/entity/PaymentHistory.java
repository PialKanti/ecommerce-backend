package com.example.ecommerce.backend.payment.entity;

import com.example.ecommerce.backend.common.entity.Auditable;
import com.example.ecommerce.backend.common.entity.BaseEntity;
import com.example.ecommerce.backend.order.entity.Order;
import com.example.ecommerce.backend.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Persistent record of a Stripe checkout payment attempt for an order.
 *
 * <p>Each record stores the Stripe Checkout Session identifier and hosted
 * checkout URL created for a single payment attempt. Status transitions are
 * handled by the payment service so future webhook, redirect, or scheduler
 * integrations can update the same history record consistently.</p>
 *
 * @author Pial Kanti Samadder
 */
@Entity
@Table(name = "payment_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistory extends BaseEntity implements Auditable {
    /**
     * Order associated with this payment attempt.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Stripe Checkout Session identifier.
     */
    @Column(name = "session_id", nullable = false, unique = true, length = 255)
    private String sessionId;

    /**
     * Stripe hosted Checkout URL returned to the client for redirection.
     */
    @Column(name = "payment_link", nullable = false, length = 2048)
    private String paymentLink;

    /**
     * Current status of this payment attempt.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PaymentStatus status;

    /**
     * Timestamp after which the payment attempt is no longer valid.
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Timestamp when the payment record was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the payment record was last modified.
     */
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    /**
     * Identifier of the user that created the payment record.
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * Identifier of the user that last modified the payment record.
     */
    @Column(name = "modified_by")
    private Long modifiedBy;
}
