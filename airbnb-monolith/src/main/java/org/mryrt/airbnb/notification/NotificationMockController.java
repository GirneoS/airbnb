package org.mryrt.airbnb.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationMockController {

    private final StompNotificationSender stompSender;

    @Value("${notification.mock.destination.mail}")
    private String mockDestinationMail;

    @PostMapping("/mock")
    public ResponseEntity<String> sendMock() {
        NotificationPayload payload = new NotificationPayload(
                mockDestinationMail,
                "Mock notification",
                "This is a test message sent to verify the message broker is working."
        );
        try {
            stompSender.send(payload);
            log.info("Mock notification sent to {}", mockDestinationMail);
            return ResponseEntity.ok("Mock notification sent to " + mockDestinationMail);
        } catch (Exception e) {
            log.error("Failed to send mock notification: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Failed: " + e.getMessage());
        }
    }
}
