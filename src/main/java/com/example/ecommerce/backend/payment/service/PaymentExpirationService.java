package com.example.ecommerce.backend.payment.service;

import com.example.ecommerce.backend.order.entity.Order;
import com.example.ecommerce.backend.order.entity.OrderStatus;
import com.example.ecommerce.backend.order.repository.OrderRepository;
import com.example.ecommerce.backend.order.service.OrderCancellationService;
import com.example.ecommerce.backend.payment.entity.PaymentHistory;
import com.example.ecommerce.backend.payment.enums.PaymentStatus;
import com.example.ecommerce.backend.payment.repository.PaymentHistoryRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentExpirationService {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final OrderRepository orderRepository;
    private final OrderCancellationService orderCancellationService;

    @Transactional(readOnly = true)
    public List<PaymentHistory> findExpiredPayments(LocalDateTime now) {
        return paymentHistoryRepository.findExpiredPayments(
                PaymentStatus.INITIATED,
                OrderStatus.CONFIRMED,
                now
        );
    }

    @Transactional
    public void expirePayment(Long paymentHistoryId) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findById(paymentHistoryId)
                .orElse(null);

        if (paymentHistory == null) {
            log.info("Expired payment skipped because payment history no longer exists. paymentHistoryId={}", paymentHistoryId);
            return;
        }

        Order order = paymentHistory.getOrder();
        if (paymentHistory.getStatus() != PaymentStatus.INITIATED) {
            log.info("Expired payment skipped because status changed. paymentHistoryId={}, status={}",
                    paymentHistoryId, paymentHistory.getStatus());
            return;
        }

        if (paymentHistory.getExpiresAt().isAfter(LocalDateTime.now())) {
            log.info("Expired payment skipped because expiration moved into the future. paymentHistoryId={}", paymentHistoryId);
            return;
        }

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            log.info("Expired payment skipped because order status changed. paymentHistoryId={}, orderId={}, status={}",
                    paymentHistoryId, order.getId(), order.getStatus());
            return;
        }

        if (hasNewerInitiatedPayment(paymentHistory)) {
            expireStripeSession(paymentHistory.getSessionId());

            paymentHistory.setStatus(PaymentStatus.CANCELLED);
            paymentHistory.setModifiedBy(order.getUserId());
            paymentHistoryRepository.save(paymentHistory);

            log.info("Expired payment cancelled without cancelling order because a newer payment exists. paymentHistoryId={}, orderId={}",
                    paymentHistoryId, order.getId());
            return;
        }

        expireStripeSession(paymentHistory.getSessionId());
        orderCancellationService.cancelConfirmedOrder(order, order.getUserId());
        paymentHistory.setStatus(PaymentStatus.CANCELLED);
        paymentHistory.setModifiedBy(order.getUserId());

        orderRepository.save(order);
        paymentHistoryRepository.save(paymentHistory);
        log.info("Expired payment processed. paymentHistoryId={}, orderId={}", paymentHistoryId, order.getId());
    }

    private void expireStripeSession(String sessionId) {
        try {
            Session.retrieve(sessionId).expire();
        } catch (StripeException exception) {
            log.warn("Unable to expire Stripe checkout session. sessionId={}", sessionId, exception);
        }
    }

    private boolean hasNewerInitiatedPayment(PaymentHistory paymentHistory) {
        return paymentHistoryRepository
                .findTopByOrderIdAndStatusOrderByCreatedAtDesc(
                        paymentHistory.getOrder().getId(),
                        PaymentStatus.INITIATED
                )
                .map(latestPayment -> !latestPayment.getId().equals(paymentHistory.getId()))
                .orElse(false);
    }
}
