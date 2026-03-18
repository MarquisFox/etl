package ru.practice.etl.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.practice.etl.service.SyncService;

@Component
public class SyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(SyncScheduler.class);
    private final SyncService syncService;

    public SyncScheduler(SyncService syncService) {
        this.syncService = syncService;
    }

    @Scheduled(cron = "${sync.cron}")
    public void runReplication() {
        log.info("Scheduled replication triggered");
        syncService.replicate();
    }
}
