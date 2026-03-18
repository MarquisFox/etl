package ru.practice.etl.repository.postgres.impl;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practice.etl.dto.ProductDto;
import ru.practice.etl.mappers.ProductMapper;
import ru.practice.etl.repository.postgres.ProductRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcProductRepository implements ProductRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ProductMapper productMapper;

    public JdbcProductRepository(JdbcTemplate jdbcTemplate, ProductMapper productMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductDto> findChanges(LocalDateTime since, int limit, long offset) {
        String sql = """
            SELECT id, name, price, updated_at, deleted_at
            FROM products
            WHERE updated_at > ?
            ORDER BY id
            LIMIT ? OFFSET ?
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> productMapper.map(rs), since, limit, offset);
    }
}