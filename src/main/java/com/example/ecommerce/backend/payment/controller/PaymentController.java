package com.example.ecommerce.backend.payment.controller;

import com.example.ecommerce.backend.common.constants.ApiEndpoints;
import com.example.ecommerce.backend.common.dto.response.ApiResponse;
import com.example.ecommerce.backend.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling Stripe Checkout payment redirects.
 *
 * <p>Exposes versioned payment endpoints under
 * {@link ApiEndpoints.Payment#BASE_PAYMENT} and wraps successful responses
 * with the common {@link ApiResponse} structure used by the API.</p>
 *
 * <p>Stripe redirects customers to these endpoints after hosted checkout
 * completes successfully or fails/cancels, allowing the payment service to
 * apply the correct payment state transition.</p>
 *
 * @author Pial Kanti Samadder
 */
@RestController
@RequestMapping(ApiEndpoints.Payment.BASE_PAYMENT)
@RequiredArgsConstructor
@Tag(
        name = "Payment",
        description = "Operations for Stripe payment callbacks"
)
public class PaymentController {
    private final PaymentService paymentService;

    /**
     * Handles a successful Stripe Checkout redirect.
     *
     * @param sessionId Stripe Checkout Session identifier
     * @return response confirming that the successful payment redirect was handled
     */
    @Operation(
            summary = "Handle successful payment",
            description = "Handles the Stripe Checkout success redirect and marks the matching payment session as successful.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Payment redirect handled successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Missing or invalid session_id",
                            content = @Content
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Payment history not found for the session",
                            content = @Content
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "Invalid payment state transition",
                            content = @Content
                    )
            }
    )
    @GetMapping("/success")
    public ResponseEntity<ApiResponse<String>> handleSuccessfulPayment(
            @Parameter(description = "Stripe Checkout Session identifier.", example = "cs_test_a1b2c3", required = true)
            @RequestParam("session_id") String sessionId) {
        paymentService.handleSuccessfulPayment(sessionId);
        return ResponseEntity.ok(ApiResponse.success("Payment successful"));
    }

    /**
     * Handles a failed Stripe Checkout redirect.
     *
     * @param sessionId Stripe Checkout Session identifier
     * @return response confirming that the failed payment redirect was handled
     */
    @Operation(
            summary = "Handle failed payment",
            description = "Handles the Stripe Checkout failed redirect and marks the matching payment session as failed.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Payment redirect handled successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Missing or invalid session_id",
                            content = @Content
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Payment history not found for the session",
                            content = @Content
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "Invalid payment state transition",
                            content = @Content
                    )
            }
    )
    @GetMapping("/failed")
    public ResponseEntity<ApiResponse<String>> handleFailedPayment(
            @Parameter(description = "Stripe Checkout Session identifier.", example = "cs_test_a1b2c3", required = true)
            @RequestParam("session_id") String sessionId) {
        paymentService.handleFailedPayment(sessionId);
        return ResponseEntity.ok(ApiResponse.success("Payment failed"));
    }
}
