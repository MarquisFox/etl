package ru.practice.etl.repository.postgres.impl;

import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practice.etl.repository.postgres.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcProductRepository implements ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Map<String, @Nullable Object>> findChanges(LocalDateTime since) {
        String sql = """
            SELECT id, name, price, updated_at, deleted_at
            FROM products
            WHERE updated_at > ?
            """;
        return jdbcTemplate.queryForList(sql, since);
    }
}
