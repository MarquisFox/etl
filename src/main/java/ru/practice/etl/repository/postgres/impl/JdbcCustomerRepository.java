package ru.practice.etl.repository.postgres.impl;

import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practice.etl.dto.CustomerDto;
import ru.practice.etl.mappers.CustomerMapper;
import ru.practice.etl.repository.postgres.CustomerRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcCustomerRepository implements CustomerRepository {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerMapper customerMapper;

    public JdbcCustomerRepository(JdbcTemplate jdbcTemplate, CustomerMapper customerMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerMapper = customerMapper;
    }
    @Override
    public List<CustomerDto> findChanges(LocalDateTime since, int limit, long offset) {
        String sql = """
            SELECT id, name, email, updated_at, deleted_at
            FROM customers
            WHERE updated_at > ?
            ORDER BY id
            LIMIT ? OFFSET ?
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> customerMapper.map(rs), since, limit, offset);
    }
}
