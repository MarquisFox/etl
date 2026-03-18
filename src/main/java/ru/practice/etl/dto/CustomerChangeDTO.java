package ru.practice.etl.dto;

import java.time.LocalDateTime;

public record CustomerChangeDTO(
        Long id,
        String name,
        String email,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
