package ru.practice.etl.repository.postgres;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Repository;
import ru.practice.etl.dto.ProductDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface ProductRepository {
    List<ProductDto> findChanges(LocalDateTime since, int limit, long offset);
}
