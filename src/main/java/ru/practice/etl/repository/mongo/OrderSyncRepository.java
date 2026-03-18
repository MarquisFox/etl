package ru.practice.etl.repository.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.springframework.stereotype.Repository;
import ru.practice.etl.dto.OrderDto;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderSyncRepository {

    private final MongoCollection<Document> customers;
    public OrderSyncRepository(MongoClient mongoClient) {
        this.customers = mongoClient.getDatabase("replica").getCollection("customers");
    }

    public void bulkUpsertOrders(List<OrderDto> orderList) {
        if (orderList.isEmpty()) return;

        List<WriteModel<Document>> bulkOperations = new ArrayList<>();

        for (OrderDto order : orderList) {
            if (order.deletedAt() != null) {
                bulkOperations.add(new UpdateOneModel<>(
                        Filters.eq("_id", order.customerId()),
                        Updates.pull("orders", Filters.eq("order_id", order.orderId()))
                ));
            } else {
                Document orderDoc = new Document("order_id", order.orderId())
                        .append("status", order.status())
                        .append("placed_at", order.placedAt())
                        .append("updated_at", order.updatedAt())
                        .append("items", order.items().stream()
                                .map(item -> new Document("product_id", item.productId())
                                        .append("product_name", item.productName())
                                        .append("product_price", item.productPrice())
                                        .append("quantity", item.quantity()))
                                .toList());

                bulkOperations.add(new UpdateOneModel<>(
                        Filters.and(
                                Filters.eq("_id", order.customerId()),
                                Filters.eq("orders.order_id", order.orderId())
                        ),
                        Updates.set("orders.$", orderDoc)
                ));

                bulkOperations.add(new UpdateOneModel<>(
                        Filters.eq("_id", order.customerId()),
                        Updates.push("orders", orderDoc),
                        new UpdateOptions().upsert(true)
                ));
            }
        }

        customers.bulkWrite(bulkOperations);
    }
}
