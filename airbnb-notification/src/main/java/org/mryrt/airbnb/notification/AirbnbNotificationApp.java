package org.mryrt.airbnb.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class AirbnbNotificationApp extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(AirbnbNotificationApp.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
        return builder
                .sources(AirbnbNotificationApp.class);
    }
}
