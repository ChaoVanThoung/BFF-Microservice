package co.istad.product_service.dto;

import lombok.Builder;

@Builder
public record ProductResponse(
        String id,
        String title,
        Double price
) {
}
