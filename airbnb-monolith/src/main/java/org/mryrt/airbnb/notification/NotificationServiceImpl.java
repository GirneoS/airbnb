package org.mryrt.airbnb.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mryrt.airbnb.auth.service.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JmsTemplate jmsTemplate;
    private final UserService userService;

    @Value("${airbnb.jms.queue.name:NotificationQueue}")
    private String queueName;

    @Override
    public void notifyUser(Long userId, NotificationType type, Map<String, Object> context) {
        try {
            String email = userService.getEntity(userId).getEmail();
            Content content = buildContent(type, context);
            jmsTemplate.convertAndSend(queueName, new NotificationPayload(email, content.title(), content.body()));
            log.debug("Notification {} queued for user {}", type, userId);
        } catch (Exception e) {
            log.warn("Failed to queue notification {} for user {}: {}", type, userId, e.getMessage());
        }
    }

    private record Content(String title, String body) {}

    private Content buildContent(NotificationType type, Map<String, Object> ctx) {
        Object bookingId    = ctx != null ? ctx.get("bookingId")    : null;
        Object listingId    = ctx != null ? ctx.get("listingId")    : null;
        Object resolutionId    = ctx != null ? ctx.get("resolutionId")    : null;
        Object amount          = ctx != null ? ctx.get("amount")          : null;
        Object confirmationUrl = ctx != null ? ctx.get("confirmationUrl") : null;

        return switch (type) {
            case BOOKING_APPLIED -> new Content(
                    "New booking request",
                    "Booking #" + bookingId + " has been submitted for your listing #" + listingId + ".");
            case BOOKING_APPROVED -> new Content(
                    "Your booking has been approved",
                    "Your booking #" + bookingId + " has been approved. Enjoy your stay!");
            case BOOKING_REJECTED -> new Content(
                    "Booking request declined",
                    "Your booking #" + bookingId + " has been declined.");
            case BOOKING_REJECTED_ANOTHER_APPROVED -> new Content(
                    "Booking request declined",
                    "Your booking #" + bookingId + " was declined — another booking was approved for the same dates.");
            case BOOKING_CHECKED_IN -> new Content(
                    "Guest checked in",
                    "Guest has checked in for booking #" + bookingId + ".");
            case BOOKING_CHECKED_OUT -> new Content(
                    "Guest checked out",
                    "Guest has checked out for booking #" + bookingId + ".");
            case LISTING_PUBLISHED -> new Content(
                    "Your listing is now published",
                    "Listing #" + listingId + " is now visible to guests.");
            case RESOLUTION_WINDOW_OPENED -> new Content(
                    "Resolution window opened",
                    "A resolution window has been opened for booking #" + bookingId + " (resolution #" + resolutionId + ").");
            case RESOLUTION_MONEY_REQUESTED -> new Content(
                    "Payment requested",
                    "The host has requested a payment of " + amount + " RUB for resolution #" + resolutionId + ".\n\n"
                    + "Click the link below to pay:\n" + confirmationUrl);
            case RESOLUTION_PAYMENT_RECEIVED -> new Content(
                    "Payment received",
                    "Payment has been received for resolution #" + resolutionId + ".");
            case RESOLUTION_GUEST_REFUSED -> new Content(
                    "Guest refused to pay",
                    "The guest refused payment for resolution #" + resolutionId + ".");
            case RESOLUTION_ESCALATED -> new Content(
                    "Resolution escalated",
                    "Resolution #" + resolutionId + " has been escalated to administration.");
            case RESOLUTION_COMPLAINT_RECORDED -> new Content(
                    "Complaint recorded",
                    "Your complaint has been recorded for resolution #" + resolutionId + ".");
            case RESOLUTION_WINDOW_CLOSED, RESOLUTION_WINDOW_CLOSED_BY_OWNER -> new Content(
                    "Resolution window closed",
                    "Resolution #" + resolutionId + " for booking #" + bookingId + " has been closed.");
            case RESOLUTION_WINDOW_CLOSED_AUTO -> new Content(
                    "Resolution window expired",
                    "Resolution #" + resolutionId + " for booking #" + bookingId + " was automatically closed after expiry.");
            case RESOLUTION_MANDATORY_PAYMENT_ASSIGNED -> new Content(
                    "Mandatory payment assigned",
                    "A mandatory payment of " + amount + " has been assigned for resolution #" + resolutionId + ".");
            case RESOLUTION_RESOLVED_WITHOUT_PAYMENT -> new Content(
                    "Resolution closed — no payment required",
                    "Resolution #" + resolutionId + " has been resolved without payment.");
        };
    }
}
