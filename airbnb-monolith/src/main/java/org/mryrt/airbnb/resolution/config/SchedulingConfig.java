package org.mryrt.airbnb.resolution.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class SchedulingConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        try {
            ScheduledExecutorService executor = InitialContext.doLookup("java:comp/DefaultManagedScheduledExecutorService");
            registrar.setScheduler(new ConcurrentTaskScheduler(executor));
        } catch (NamingException e) {
            throw new IllegalStateException("Failed to lookup managed scheduled executor", e);
        }
    }
}
