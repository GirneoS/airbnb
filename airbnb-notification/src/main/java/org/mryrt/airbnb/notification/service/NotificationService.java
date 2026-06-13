package org.mryrt.airbnb.notification.service;

public interface NotificationService {
    void send(String email, String title, String body);
}
