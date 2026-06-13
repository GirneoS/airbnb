package org.mryrt.airbnb.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mryrt.airbnb.booking.service.BookingService;
import org.mryrt.airbnb.listing.service.ListingService;
import org.mryrt.airbnb.notification.NotificationService;
import org.mryrt.airbnb.notification.NotificationType;
import org.mryrt.airbnb.resolution.model.ResolutionStatus;
import org.mryrt.airbnb.resolution.model.ResolutionWindow;
import org.mryrt.airbnb.resolution.repository.ResolutionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Мок-эндпоинт подтверждения оплаты — имитирует webhook от YooKassa.
 * В production заменяется на POST /webhooks/yookassa с верификацией подписи.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final ResolutionRepository resolutionRepository;
    private final NotificationService notificationService;
    private final BookingService bookingService;
    private final ListingService listingService;

    @GetMapping("/{paymentId}/mock-confirm")
    public ResponseEntity<String> mockConfirm(@PathVariable String paymentId) {
        Optional<ResolutionWindow> opt = resolutionRepository.findByYooPaymentId(paymentId);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ResolutionWindow window = opt.get();
        if (window.getStatus() != ResolutionStatus.MONEY_REQUESTED
                && window.getStatus() != ResolutionStatus.MANDATORY_PAYMENT) {
            return ResponseEntity.badRequest().body("Payment already processed or not awaiting payment.");
        }

        window.setStatus(ResolutionStatus.PAID);
        window.setClosedAt(LocalDateTime.now());
        resolutionRepository.save(window);
        log.info("Mock payment confirmed: paymentId={}, resolutionId={}", paymentId, window.getId());

        Long ownerId = listingService
                .getEntity(bookingService.getEntity(window.getBookingId()).getListingId())
                .getOwnerId();
        notificationService.notifyUser(ownerId, NotificationType.RESOLUTION_PAYMENT_RECEIVED,
                Map.of("bookingId", window.getBookingId(), "resolutionId", window.getId()));

        return ResponseEntity.ok("Payment confirmed. You may close this window.");
    }
}
