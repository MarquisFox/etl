package ru.practice.etl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practice.etl.repository.mongo.CustomerSyncRepository;
import ru.practice.etl.repository.mongo.OrderSyncRepository;
import ru.practice.etl.repository.mongo.ProductSyncRepository;
import ru.practice.etl.repository.postgres.CustomerRepository;
import ru.practice.etl.repository.postgres.OrderRepository;
import ru.practice.etl.repository.postgres.ProductRepository;
import ru.practice.etl.repository.postgres.SyncStateRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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

        syncEntities("customer",
                offset -> customerRepo.findChanges(lastSync, PAGE_SIZE, offset),
                customerSyncRepo::bulkUpsertCustomers);

        syncEntities("order",
                offset -> orderRepo.findChanges(lastSync, PAGE_SIZE, offset),
                orderSyncRepo::bulkUpsertOrders);

        syncEntities("product",
                offset -> productRepo.findChanges(lastSync, PAGE_SIZE, offset),
                productSyncRepo::bulkUpsertProducts);

        syncStateRepo.updateLastSyncTime(LocalDateTime.now());
        log.info("Sync completed");
    }


    private <T> void syncEntities(String entityName,
                                  Function<Long, List<T>> pageFetcher,
                                  Consumer<List<T>> upsertFunction) {
        long offset = 0;
        List<T> entities;
        do {
            entities = pageFetcher.apply(offset);
            upsertFunction.accept(entities);
            offset += entities.size();
            log.debug("Processed {} {} records", offset, entityName);
        } while (!entities.isEmpty());
    }
}