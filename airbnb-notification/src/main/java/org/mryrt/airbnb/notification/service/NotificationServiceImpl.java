package org.mryrt.airbnb.notification.service;

import lombok.RequiredArgsConstructor;
import org.mryrt.airbnb.notification.service.email.EmailService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmailService emailService;

    @Override
    public void send(String email, String title, String body) {
        emailService.send(email, title, body != null ? body : title);
    }
}
