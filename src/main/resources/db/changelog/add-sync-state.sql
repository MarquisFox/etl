CREATE TABLE sync_state (
    id          INTEGER PRIMARY KEY DEFAULT 1,
    last_sync   TIMESTAMP NOT NULL
);


INSERT INTO sync_state (last_sync) VALUES ('2026-01-01 00:00:00');