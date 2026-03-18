package ru.practice.etl.repository.postgres;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface OrderRepository {
    List<Map<String, @Nullable Object>> findChanges(LocalDateTime since);
}
