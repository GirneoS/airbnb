package org.mryrt.airbnb.notification;

import java.util.Map;

public interface NotificationService {
    void notifyUser(Long userId, NotificationType type, Map<String, Object> context);
}
