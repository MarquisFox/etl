package ru.practice.etl.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class DataGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(DataGeneratorService.class);
    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();

    @Value("${app.generate-test-data:false}")
    private boolean generateTestData;

    @Value("${app.customers.count:1000}")
    private int customersCount;

    @Value("${app.products.count:5000}")
    private int productsCount;

    @Value("${app.orders.count:50000}")
    private int ordersCount;

    public DataGeneratorService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    @Transactional
    public void generateIfNeeded() {
        if (!generateTestData) return;

        log.info("Generating test data...");
        long start = System.currentTimeMillis();

        generateCustomers();
        generateProducts();
        generateOrders();
        generateOrderItems();

        log.info("Test data generated in {} ms", System.currentTimeMillis() - start);
    }

    private void generateCustomers() {
        String sql = """
            INSERT INTO customers (name, email, created_at, updated_at)
            SELECT 'Customer ' || gs, 'customer' || gs || '@example.com',
                   NOW() - (random() * interval '365 days'),
                   NOW() - (random() * interval '30 days')
            FROM generate_series(1, ?) gs
            ON CONFLICT (email) DO NOTHING
            """;
        int inserted = jdbcTemplate.update(sql, customersCount);
        log.info("Inserted {} customers", inserted);
    }

    private void generateProducts() {
        String sql = """
            INSERT INTO products (name, price, created_at, updated_at)
            SELECT 'Product ' || gs, (random() * 1000)::numeric(10,2),
                   NOW() - (random() * interval '365 days'),
                   NOW() - (random() * interval '30 days')
            FROM generate_series(1, ?) gs
            """;
        int inserted = jdbcTemplate.update(sql, productsCount);
        log.info("Inserted {} products", inserted);
    }

    private void generateOrders() {
        String sql = """
            INSERT INTO orders (customer_id, status, created_at, updated_at)
            SELECT ceil(random() * ?), 
                   CASE WHEN random() < 0.3 THEN 'pending' 
                        WHEN random() < 0.6 THEN 'shipped' 
                        ELSE 'completed' END,
                   NOW() - (random() * interval '180 days'),
                   NOW() - (random() * interval '30 days')
            FROM generate_series(1, ?)
            """;
        int inserted = jdbcTemplate.update(sql, customersCount, ordersCount);
        log.info("Inserted {} orders", inserted);
    }

    private void generateOrderItems() {
        String sql = """
        INSERT INTO order_items (order_id, product_id, quantity, price, updated_at)
        SELECT o.id,
               p.id,
               ceil(random() * 3)::int,
               p.price,
               NOW()
        FROM orders o
        CROSS JOIN LATERAL (
            SELECT id, price
            FROM products
            ORDER BY random()
            LIMIT (1 + (random() * 4)::int)
        ) p
        """;
        int inserted = jdbcTemplate.update(sql);
        log.info("Inserted {} order items", inserted);
    }
}
