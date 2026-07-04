package com.example.ecommerce.backend.order.entity;

/**
 * Lifecycle state for customer orders.
 *
 * <p>Checkout creates orders directly in {@link #CONFIRMED} state because
 * inventory has been reserved and the order has been accepted. Payment
 * completion moves the order to {@link #PAID}; unpaid confirmed orders can be
 * cancelled later by the planned timeout scheduler.</p>
 *
 * @author Pial Kanti Samadder
 */
public enum OrderStatus {
    CREATED,
    CONFIRMED,
    PAID,
    CANCELLED
}
