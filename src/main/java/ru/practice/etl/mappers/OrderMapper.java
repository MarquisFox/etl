package ru.practice.etl.mappers;

import org.springframework.stereotype.Component;
import ru.practice.etl.dto.OrderDto;
import ru.practice.etl.dto.OrderItemDto;
import ru.practice.etl.utils.SqlUtils;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderMapper {

    private final ObjectMapper objectMapper;
    private final TypeReference<List<OrderItemDto>> itemsType = new TypeReference<>() {};

    public OrderMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OrderDto map(ResultSet rs) throws SQLException, IOException {
        Long orderId = SqlUtils.getLong(rs, "order_id");
        Long customerId = SqlUtils.getLong(rs, "customer_id");
        String status = SqlUtils.getString(rs, "status");
        LocalDateTime placedAt = SqlUtils.getLocalDateTime(rs, "placed_at");
        LocalDateTime updatedAt = SqlUtils.getLocalDateTime(rs, "updated_at");
        LocalDateTime deletedAt = SqlUtils.getLocalDateTime(rs, "deleted_at");
        String customerName = SqlUtils.getString(rs, "customer_name");
        String customerEmail = SqlUtils.getString(rs, "customer_email");
        String itemsJson = SqlUtils.getString(rs, "items");

        List<OrderItemDto> items = objectMapper.readValue(itemsJson, itemsType);

        return new OrderDto(
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
    }
}