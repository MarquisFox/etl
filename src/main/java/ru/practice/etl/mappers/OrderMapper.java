package ru.practice.etl.mappers;

import org.springframework.stereotype.Component;
import ru.practice.etl.dto.OrderChangeDTO;
import ru.practice.etl.dto.OrderDTO;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class OrderMapper {

    private final ObjectMapper objectMapper;
    private final TypeReference<List<OrderDTO>> itemsType = new TypeReference<>() {};

    public OrderMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OrderChangeDTO map(Map<String, Object> row) {
        try {
            Long orderId = ((Number) row.get("order_id")).longValue();
            Long customerId = ((Number) row.get("customer_id")).longValue();
            String status = (String) row.get("status");
            LocalDateTime placedAt = parseTimestamp(row.get("placed_at"));
            LocalDateTime updatedAt = parseTimestamp(row.get("updated_at"));
            LocalDateTime deletedAt = row.get("deleted_at") != null ? parseTimestamp(row.get("deleted_at")) : null;
            String customerName = (String) row.get("customer_name");
            String customerEmail = (String) row.get("customer_email");
            String itemsJson = (String) row.get("items");

            List<OrderDTO> items = objectMapper.readValue(itemsJson, itemsType);

            return new OrderChangeDTO(
                    orderId,
                    customerId,
                    status,
                    placedAt,
                    updatedAt,
                    deletedAt,
                    customerName,
                    customerEmail,
                    items
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse items JSON", e);
        }
    }

    private LocalDateTime parseTimestamp(Object obj) {
        if (obj instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) obj).toLocalDateTime();
        } else if (obj instanceof String) {
            return LocalDateTime.parse((String) obj); // если вдруг строка
        } else {
            throw new IllegalArgumentException("Unknown timestamp type: " + obj.getClass());
        }
    }
}