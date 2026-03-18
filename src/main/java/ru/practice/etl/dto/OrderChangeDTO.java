package ru.practice.etl.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderChangeDTO(
        Long orderId,
        Long customerId,
        String status,
        LocalDateTime placedAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        String customerName,
        String customerEmail,
        List<OrderDTO> items
) {}
