package ru.practice.etl.repository.postgres;

import java.time.LocalDateTime;

public interface SyncStateRepository {
    LocalDateTime getLastSyncTime();

    void updateLastSyncTime(LocalDateTime time);
}
