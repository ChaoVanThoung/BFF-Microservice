package co.istad.product_service.dto;

import lombok.Builder;

@Builder
public record ProductRequest(
        String title,
        Double price
) {
}
