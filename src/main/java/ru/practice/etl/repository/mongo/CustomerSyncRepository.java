package ru.practice.etl.repository.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.springframework.stereotype.Repository;
import ru.practice.etl.dto.CustomerDto;
import ru.practice.etl.dto.OrderDto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerSyncRepository {

    private final MongoCollection<Document> customers;

    public CustomerSyncRepository(MongoClient mongoClient) {
        this.customers = mongoClient.getDatabase("replica").getCollection("customers");
    }

    public void bulkUpsertCustomers(List<CustomerDto> customerList) {
        if (customerList.isEmpty()) return;

        List<WriteModel<Document>> bulkOperations = new ArrayList<>();

        for (CustomerDto cust : customerList) {
            if (cust.deletedAt() != null) {
                bulkOperations.add(new DeleteOneModel<>(Filters.eq("_id", cust.id())));
            } else {
                Document doc = new Document("_id", cust.id())
                        .append("name", cust.name())
                        .append("email", cust.email())
                        .append("updated_at", cust.updatedAt())
                        .append("synced_at", Instant.now());

                bulkOperations.add(new ReplaceOneModel<>(
                        Filters.eq("_id", cust.id()),
                        doc,
                        new ReplaceOptions().upsert(true)
                ));
            }
        }

        customers.bulkWrite(bulkOperations);
    }
}