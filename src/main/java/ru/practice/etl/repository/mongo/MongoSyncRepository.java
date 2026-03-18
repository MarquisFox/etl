package ru.practice.etl.repository.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ru.practice.etl.dto.CustomerChangeDTO;
import ru.practice.etl.dto.OrderChangeDTO;
import ru.practice.etl.dto.ProductChangeDTO;

import java.time.Instant;
import java.util.List;

@Repository
public class MongoSyncRepository {

    private final MongoCollection<Document> customers;
    private final MongoCollection<Document> products;

    public MongoSyncRepository(MongoClient mongoClient) {
        var database = mongoClient.getDatabase("replica");
        this.customers = database.getCollection("customers");
        this.products = database.getCollection("products");
    }

    public void upsertCustomer(CustomerChangeDTO cust) {
        if (cust.deletedAt() != null) {
            customers.deleteOne(Filters.eq("_id", cust.id()));
            return;
        }

        var update = Updates.combine(
                Updates.set("name", cust.name()),
                Updates.set("email", cust.email()),
                Updates.set("synced_at", Instant.now())
        );
        customers.updateOne(
                Filters.eq("_id", cust.id()),
                update,
                new UpdateOptions().upsert(true)
        );
    }

    public void upsertOrder(OrderChangeDTO order) {
        if (order.deletedAt() != null) {
            customers.updateOne(
                    Filters.eq("_id", order.customerId()),
                    Updates.pull("orders", Filters.eq("order_id", order.orderId()))
            );
            return;
        }

        Document orderDoc = new Document("order_id", order.orderId())
                .append("status", order.status())
                .append("placed_at", order.placedAt())
                .append("items", order.items().stream()
                        .map(item -> new Document("product_id", item.productId())
                                .append("product_name", item.productName())
                                .append("product_price", item.productPrice())
                                .append("quantity", item.quantity()))
                        .toList());

        var updateResult = customers.updateOne(
                Filters.and(
                        Filters.eq("_id", order.customerId()),
                        Filters.eq("orders.order_id", order.orderId())
                ),
                Updates.set("orders.$", orderDoc)
        );

        if (updateResult.getMatchedCount() == 0) {
            customers.updateOne(
                    Filters.eq("_id", order.customerId()),
                    Updates.push("orders", orderDoc),
                    new UpdateOptions().upsert(true)
            );
        }
    }

    public void upsertProduct(ProductChangeDTO prod) {
        if (prod.deletedAt() != null) {
            products.deleteOne(Filters.eq("_id", prod.id()));
            return;
        }

        Document doc = new Document("_id", prod.id())
                .append("name", prod.name())
                .append("price", prod.price())
                .append("updated_at", prod.updatedAt());

        products.replaceOne(
                Filters.eq("_id", prod.id()),
                doc,
                new ReplaceOptions().upsert(true)
        );
    }
}