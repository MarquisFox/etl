package ru.practice.etl.mappers;

import org.springframework.stereotype.Component;
import ru.practice.etl.dto.CustomerDto;
import ru.practice.etl.utils.SqlUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

@Component
public class CustomerMapper {

    public CustomerDto map(ResultSet rs) throws SQLException {
        return new CustomerDto(
                SqlUtils.getLong(rs, "id"),
                SqlUtils.getString(rs, "name"),
                SqlUtils.getString(rs, "email"),
                SqlUtils.getLocalDateTime(rs, "updated_at"),
                SqlUtils.getLocalDateTime(rs, "deleted_at")
        );
    }
}