package org.mryrt.airbnb.resolution.config;

import lombok.RequiredArgsConstructor;
import org.mryrt.airbnb.resolution.service.ResolutionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResolutionScheduler {

    private final ResolutionService resolutionService;

    @Scheduled(cron = "0 */1 * * * *")
    public void closeExpiredResolutionWindows() {
        resolutionService.closeExpiredWindows();
        System.out.println("Resolution scheduler: closeExpiredWindows completed");
    }
}
