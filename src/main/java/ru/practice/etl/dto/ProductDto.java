package ru.practice.etl.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductDto(
        Long id,
        String name,
        BigDecimal price,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
