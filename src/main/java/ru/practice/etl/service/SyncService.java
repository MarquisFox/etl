package ru.practice.etl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practice.etl.dto.CustomerDto;
import ru.practice.etl.dto.OrderDto;
import ru.practice.etl.dto.ProductDto;
import ru.practice.etl.repository.mongo.CustomerSyncRepository;
import ru.practice.etl.repository.mongo.OrderSyncRepository;
import ru.practice.etl.repository.mongo.ProductSyncRepository;
import ru.practice.etl.repository.postgres.CustomerRepository;
import ru.practice.etl.repository.postgres.OrderRepository;
import ru.practice.etl.repository.postgres.ProductRepository;
import ru.practice.etl.repository.postgres.SyncStateRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);
    private static final int PAGE_SIZE = 1000;

    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final CustomerSyncRepository customerSyncRepo;
    private final OrderSyncRepository orderSyncRepo;
    private final ProductSyncRepository productSyncRepo;
    private final SyncStateRepository syncStateRepo;

    public SyncService(CustomerRepository customerRepo, OrderRepository orderRepo,
                       ProductRepository productRepo,
                       CustomerSyncRepository customerSyncRepo,
                       OrderSyncRepository orderSyncRepo,
                       ProductSyncRepository productSyncRepo,
                       SyncStateRepository syncStateRepo) {
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.customerSyncRepo = customerSyncRepo;
        this.orderSyncRepo = orderSyncRepo;
        this.productSyncRepo = productSyncRepo;
        this.syncStateRepo = syncStateRepo;
    }

    public void replicate() {
        LocalDateTime lastSync = syncStateRepo.getLastSyncTime();
        log.info("Starting sync from {}", lastSync);

        long customerOffset = 0;
        List<CustomerDto> customers;
        do {
            customers = customerRepo.findChanges(lastSync, PAGE_SIZE, customerOffset);
            customerSyncRepo.bulkUpsertCustomers(customers);
            customerOffset += customers.size();
            log.debug("Processed {} customers", customerOffset);
        } while (!customers.isEmpty());

        long orderOffset = 0;
        List<OrderDto> orders;
        do {
            orders = orderRepo.findChanges(lastSync, PAGE_SIZE, orderOffset);
            orderSyncRepo.bulkUpsertOrders(orders);
            orderOffset += orders.size();
            log.debug("Processed {} orders", orderOffset);
        } while (!orders.isEmpty());

        long productOffset = 0;
        List<ProductDto> products;
        do {
            products = productRepo.findChanges(lastSync, PAGE_SIZE, productOffset);
            productSyncRepo.bulkUpsertProducts(products);
            productOffset += products.size();
            log.debug("Processed {} products", productOffset);
        } while (!products.isEmpty());

        syncStateRepo.updateLastSyncTime(LocalDateTime.now());
        log.info("Sync completed");
    }
}