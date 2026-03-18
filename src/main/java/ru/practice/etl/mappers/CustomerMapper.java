package ru.practice.etl.mappers;

import org.springframework.stereotype.Component;
import ru.practice.etl.dto.CustomerChangeDTO;

import java.sql.Timestamp;
import java.util.Map;

@Component
public class CustomerMapper {

    public CustomerChangeDTO map(Map<String, Object> row) {
        return new CustomerChangeDTO(
                ((Number) row.get("id")).longValue(),
                (String) row.get("name"),
                (String) row.get("email"),
                ((Timestamp) row.get("updated_at")).toLocalDateTime(),
                row.get("deleted_at") != null
                        ? ((Timestamp) row.get("deleted_at")).toLocalDateTime()
                        : null
        );
    }
}