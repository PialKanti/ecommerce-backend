package com.example.ecommerce.backend.payment.dto.response;

import com.example.ecommerce.backend.payment.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Response payload describing a Stripe payment attempt for an order.
 *
 * <p>The checkout URL is returned to the API client so the customer can be
 * redirected to Stripe hosted Checkout. Status changes are persisted in
 * payment history and exposed here for clients that need immediate checkout
 * state.</p>
 *
 * @author Pial Kanti Samadder
 */
@Schema(description = "Stripe payment attempt details for a customer order.")
public record PaymentResponse(
        @Schema(description = "Payment history identifier.", example = "1")
        Long id,

        @Schema(description = "Order identifier linked to this payment attempt.", example = "10")
        Long orderId,

        @Schema(description = "Stripe Checkout Session identifier.", example = "cs_test_a1b2c3")
        String sessionId,

        @Schema(description = "Stripe hosted Checkout URL where the customer should be redirected.")
        String paymentLink,

        @Schema(description = "Current payment lifecycle status.", example = "INITIATED")
        PaymentStatus status,

        @Schema(description = "Timestamp when the payment attempt expires.")
        LocalDateTime expiresAt,

        @Schema(description = "Timestamp when the payment attempt was created.")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when the payment attempt was last modified.")
        LocalDateTime modifiedAt
) {
}
