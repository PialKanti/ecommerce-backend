package com.example.ecommerce.backend.order.service;

import com.example.ecommerce.backend.order.dto.request.CreateOrderRequest;
import com.example.ecommerce.backend.order.dto.response.OrderCheckoutResponse;
import com.example.ecommerce.backend.order.dto.response.OrderResponse;

/**
 * Service interface for checkout and order lifecycle operations.
 *
 * <p>Order operations coordinate cart contents, product snapshots, inventory
 * reservations, order persistence, and the post-checkout payment session
 * returned to the client.</p>
 *
 * @author Pial Kanti Samadder
 */
public interface OrderService {
    /**
     * Converts a cart into a confirmed order, reserves inventory, and creates
     * the Stripe payment session for the order.
     *
     * @param userId owner user identifier
     * @param request checkout payload containing cart identifier
     * @return checkout response containing the confirmed order and payment link
     */
    OrderCheckoutResponse placeOrder(Long userId, CreateOrderRequest request);

    /**
     * Cancels a confirmed order and releases reserved inventory.
     *
     * @param userId owner user identifier
     * @param orderId order identifier
     * @return cancelled order response
     */
    OrderResponse cancelOrder(Long userId, Long orderId);

    /**
     * Retrieves an order by identifier.
     *
     * @param userId owner user identifier
     * @param orderId order identifier
     * @return matching order response
     */
    OrderResponse getOrder(Long userId, Long orderId);

    /**
     * Retrieves any order for administrative or support workflows.
     *
     * @param orderId order identifier
     * @return matching order response
     */
    OrderResponse getOrderForAdministration(Long orderId);

    /**
     * Cancels any confirmed order for administrative or support workflows.
     *
     * @param actorUserId authenticated administrator or support user identifier
     * @param orderId order identifier
     * @return cancelled order response
     */
    OrderResponse cancelOrderForAdministration(Long actorUserId, Long orderId);
}
