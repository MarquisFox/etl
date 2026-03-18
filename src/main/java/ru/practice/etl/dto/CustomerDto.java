package ru.practice.etl.dto;

import java.time.LocalDateTime;

public record CustomerDto(
        Long id,
        String name,
        String email,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
