package com.example.ecommerce.backend.payment.enums;

/**
 * Lifecycle state for a Stripe payment attempt.
 *
 * <p>Payment starts as {@link #INITIATED} after a Checkout Session is created.
 * Successful checkout moves it to {@link #SUCCESS}; failed or cancelled Stripe
 * checkout attempts can move it to {@link #FAILED}. {@link #CANCELLED} is
 * reserved for future order timeout or manual cancellation workflows.</p>
 *
 * @author Pial Kanti Samadder
 */
public enum PaymentStatus {
    INITIATED,
    SUCCESS,
    FAILED,
    CANCELLED
}
