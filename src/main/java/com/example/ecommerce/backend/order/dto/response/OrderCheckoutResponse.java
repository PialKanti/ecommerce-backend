package com.example.ecommerce.backend.order.dto.response;

import com.example.ecommerce.backend.payment.dto.response.PaymentResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload returned after checkout completes.
 *
 * <p>Combines the confirmed order snapshot with the Stripe payment session
 * needed by the client to redirect the customer to hosted Checkout.</p>
 *
 * @author Pial Kanti Samadder
 */
@Schema(description = "Checkout result containing the confirmed order and Stripe payment session.")
public record OrderCheckoutResponse(
        @Schema(description = "Confirmed order created from the checked-out cart.")
        OrderResponse order,

        @Schema(description = "Stripe payment session created for the order.")
        PaymentResponse payment
) {
}
