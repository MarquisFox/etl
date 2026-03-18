package ru.practice.etl.mappers;

import org.springframework.stereotype.Component;
import ru.practice.etl.dto.ProductChangeDTO;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;

@Component
public class ProductMapper {

    public ProductChangeDTO map(Map<String, Object> row) {
        return new ProductChangeDTO(
                ((Number) row.get("id")).longValue(),
                (String) row.get("name"),
                (BigDecimal) row.get("price"),
                ((Timestamp) row.get("updated_at")).toLocalDateTime(),
                row.get("deleted_at") != null
                        ? ((Timestamp) row.get("deleted_at")).toLocalDateTime()
                        : null
        );
    }
}
