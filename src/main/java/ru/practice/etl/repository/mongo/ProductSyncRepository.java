package ru.practice.etl.repository.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.springframework.stereotype.Repository;
import ru.practice.etl.dto.ProductDto;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductSyncRepository {

    private final MongoCollection<Document> products;

    public ProductSyncRepository(MongoClient mongoClient) {
        this.products = mongoClient.getDatabase("replica").getCollection("products");
    }

    public void bulkUpsertProducts(List<ProductDto> productList) {
        if (productList.isEmpty()) return;

        List<WriteModel<Document>> bulkOperations = new ArrayList<>();

        for (ProductDto prod : productList) {
            if (prod.deletedAt() != null) {
                bulkOperations.add(new DeleteOneModel<>(Filters.eq("_id", prod.id())));
            } else {
                Document doc = new Document("_id", prod.id())
                        .append("name", prod.name())
                        .append("price", prod.price())
                        .append("updated_at", prod.updatedAt());

                bulkOperations.add(new ReplaceOneModel<>(
                        Filters.eq("_id", prod.id()),
                        doc,
                        new ReplaceOptions().upsert(true)
                ));
            }
        }

        products.bulkWrite(bulkOperations);
    }
}