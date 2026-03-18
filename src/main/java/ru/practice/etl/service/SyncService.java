package ru.practice.etl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.practice.etl.dto.CustomerChangeDTO;
import ru.practice.etl.dto.OrderChangeDTO;
import ru.practice.etl.dto.ProductChangeDTO;
import ru.practice.etl.mappers.CustomerMapper;
import ru.practice.etl.mappers.OrderMapper;
import ru.practice.etl.mappers.ProductMapper;
import ru.practice.etl.repository.mongo.MongoSyncRepository;
import ru.practice.etl.repository.postgres.CustomerRepository;
import ru.practice.etl.repository.postgres.OrderRepository;
import ru.practice.etl.repository.postgres.ProductRepository;
import ru.practice.etl.repository.postgres.SyncStateRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final MongoSyncRepository mongoRepo;
    private final SyncStateRepository syncStateRepo;
    private final CustomerMapper customerMapper;
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final JdbcTemplate jdbcTemplate;

    public SyncService(CustomerRepository customerRepo, OrderRepository orderRepo,
                       ProductRepository productRepo, MongoSyncRepository mongoRepo,
                       SyncStateRepository syncStateRepo,
                       CustomerMapper customerMapper, OrderMapper orderMapper,
                       ProductMapper productMapper, JdbcTemplate jdbcTemplate) {
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.mongoRepo = mongoRepo;
        this.syncStateRepo = syncStateRepo;
        this.customerMapper = customerMapper;
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void replicate() {
        LocalDateTime lastSync = syncStateRepo.getLastSyncTime();
        log.info("Starting sync from {}", lastSync);

        List<Map<String, Object>> customerRows = customerRepo.findChanges(lastSync);
        List<Map<String, Object>> orderRows = orderRepo.findChanges(lastSync);
        List<Map<String, Object>> productRows = productRepo.findChanges(lastSync);

        List<CustomerChangeDTO> customers = customerRows.stream()
                .map(customerMapper::map)
                .collect(Collectors.toList());

        List<OrderChangeDTO> orders = orderRows.stream()
                .map(orderMapper::map)
                .collect(Collectors.toList());

        List<ProductChangeDTO> products = productRows.stream()
                .map(productMapper::map)
                .collect(Collectors.toList());

        customers.forEach(mongoRepo::upsertCustomer);
        orders.forEach(mongoRepo::upsertOrder);
        products.forEach(mongoRepo::upsertProduct);

        syncStateRepo.updateLastSyncTime(LocalDateTime.now());
        log.info("Sync completed: {} customers, {} orders, {} products",
                customers.size(), orders.size(), products.size());
    }
}