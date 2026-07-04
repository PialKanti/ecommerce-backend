package com.example.ecommerce.backend.payment.service;

import com.example.ecommerce.backend.payment.dto.response.PaymentResponse;

/**
 * Service contract for creating Stripe checkout sessions and updating payment
 * outcomes.
 *
 * <p>The payment module intentionally has no controller in this phase. Other
 * application services can initiate payments after checkout, and future
 * webhook, redirect, or scheduler components can call the status handlers.</p>
 *
 * @author Pial Kanti Samadder
 */
public interface PaymentService {
    /**
     * Creates a Stripe checkout session for a payable order and stores the
     * payment attempt.
     *
     * @param orderId order identifier
     * @return payment details containing the Stripe checkout URL
     */
    PaymentResponse initiatePayment(Long orderId);

    /**
     * Marks a Stripe checkout session as successfully paid and updates the
     * linked order to paid.
     *
     * @param sessionId Stripe checkout session identifier
     */
    void handleSuccessfulPayment(String sessionId);

    /**
     * Marks a Stripe checkout session as failed without cancelling the linked
     * order, allowing the future timeout scheduler to own order cancellation.
     *
     * @param sessionId Stripe checkout session identifier
     */
    void handleFailedPayment(String sessionId);
}
