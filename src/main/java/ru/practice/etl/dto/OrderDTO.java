package ru.practice.etl.dto;

import java.math.BigDecimal;

public record OrderDTO(
        Long productId,
        String productName,
        BigDecimal productPrice,
        Integer quantity
) {}