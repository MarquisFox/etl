package ru.practice.etl.repository.postgres.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practice.etl.repository.postgres.SyncStateRepository;

import java.time.LocalDateTime;

@Repository
public class JdbcSyncStateRepository implements SyncStateRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSyncStateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public LocalDateTime getLastSyncTime() {
        return jdbcTemplate.queryForObject(
                "SELECT last_sync FROM sync_state WHERE id = 1",
                LocalDateTime.class);
    }

    public void updateLastSyncTime(LocalDateTime time) {
        jdbcTemplate.update(
                "UPDATE sync_state SET last_sync = ? WHERE id = 1", time);
    }
}