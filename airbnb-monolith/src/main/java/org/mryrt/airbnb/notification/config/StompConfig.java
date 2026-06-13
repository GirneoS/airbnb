package org.mryrt.airbnb.notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mryrt.airbnb.notification.StompNotificationSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StompConfig {

    @Value("${airbnb.stomp.host:localhost}")
    private String host;

    @Value("${airbnb.stomp.port:61613}")
    private int port;

    @Value("${airbnb.stomp.username}")
    private String username;

    @Value("${airbnb.stomp.password}")
    private String password;

    @Value("${airbnb.stomp.destination:NotificationQueue}")
    private String destination;

    @Bean
    public StompNotificationSender stompNotificationSender(ObjectMapper objectMapper) {
        return new StompNotificationSender(host, port, username, password, destination, objectMapper);
    }
}
