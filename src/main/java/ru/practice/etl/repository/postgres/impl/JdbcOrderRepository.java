package ru.practice.etl.repository.postgres.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practice.etl.repository.postgres.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcOrderRepository implements OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Map<String, Object>> findChanges(LocalDateTime since) {
        String sql = """
            SELECT
                o.id AS order_id,
                o.customer_id,
                o.status,
                o.created_at AS placed_at,
                o.updated_at,
                o.deleted_at,
                c.name AS customer_name,
                c.email AS customer_email,
                COALESCE(
                    (SELECT json_agg(
                        json_build_object(
                            'productId', p.id,
                            'productName', p.name,
                            'productPrice', oi.price,
                            'quantity', oi.quantity
                        )
                    ) FROM order_items oi
                    JOIN products p ON p.id = oi.product_id
                    WHERE oi.order_id = o.id),
                    '[]'::json
                )::text AS items   -- возвращаем как текст, чтобы Jackson мог распарсить
            FROM orders o
            JOIN customers c ON c.id = o.customer_id
            WHERE o.updated_at > ?
            """;
        return jdbcTemplate.queryForList(sql, since);
    }
}