package ru.practice.etl.repository.postgres.impl;

import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practice.etl.repository.postgres.CustomerRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcCustomerRepository implements CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Map<String, @Nullable Object>> findChanges(LocalDateTime since) {
        String sql = """
            SELECT id, name, email, updated_at, deleted_at
            FROM customers
            WHERE updated_at > ?
            """;
        return jdbcTemplate.queryForList(sql, since);
    }
}
