package ru.practice.etl.repository.postgres;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Repository;
import ru.practice.etl.dto.CustomerDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface CustomerRepository {
    List<CustomerDto> findChanges(LocalDateTime since, int limit, long offset);
}
