package ru.practice.etl.mappers;

import org.springframework.stereotype.Component;
import ru.practice.etl.dto.ProductDto;
import ru.practice.etl.utils.SqlUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

@Component
public class ProductMapper {

    public ProductDto map(ResultSet rs) throws SQLException {
        return new ProductDto(
                SqlUtils.getLong(rs, "id"),
                SqlUtils.getString(rs, "name"),
                SqlUtils.getBigDecimal(rs, "price"),
                SqlUtils.getLocalDateTime(rs, "updated_at"),
                SqlUtils.getLocalDateTime(rs, "deleted_at")
        );
    }
}
