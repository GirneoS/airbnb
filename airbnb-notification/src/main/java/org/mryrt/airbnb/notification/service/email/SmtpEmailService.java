package org.mryrt.airbnb.notification.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpEmailService implements EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${airbnb.mail.from:airbnb@localhost}")
    private String fromAddress;

    @Value("${airbnb.mail.enabled:true}")
    private boolean enabled;

    @Override
    public void send(String to, String subject, String body) {
        if (to == null || to.isBlank()) return;
        if (!enabled || mailSender == null) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
