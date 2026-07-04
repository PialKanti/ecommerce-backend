package com.example.ecommerce.backend.payment.scheduler;

import com.example.ecommerce.backend.payment.entity.PaymentHistory;
import com.example.ecommerce.backend.payment.service.PaymentExpirationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentExpirationScheduler {
    private final PaymentExpirationService paymentExpirationService;

    @Scheduled(fixedDelayString = "${payment.expiration.check-delay}")
    public void expireOverduePayments() {
        LocalDateTime now = LocalDateTime.now();
        List<PaymentHistory> expiredPayments = paymentExpirationService.findExpiredPayments(now);

        if (expiredPayments.isEmpty()) {
            return;
        }

        log.info("Processing expired payments. count={}", expiredPayments.size());
        expiredPayments.forEach(paymentHistory -> expirePayment(paymentHistory.getId()));
    }

    private void expirePayment(Long paymentHistoryId) {
        try {
            paymentExpirationService.expirePayment(paymentHistoryId);
        } catch (RuntimeException exception) {
            log.error("Expired payment processing failed. paymentHistoryId={}", paymentHistoryId, exception);
        }
    }
}
