package org.mryrt.airbnb.notification.listener;

import lombok.RequiredArgsConstructor;
import org.mryrt.airbnb.notification.dto.EmailMessage;
import org.mryrt.airbnb.notification.service.NotificationService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @JmsListener(
            destination = "${airbnb.jms.queue.name:java:/jms/queue/NotificationQueue}",
            containerFactory = "jmsListenerContainerFactory"
    )
    public void onMessage(EmailMessage message) {
        notificationService.send(message.email(), message.title(), message.body());
    }
}
