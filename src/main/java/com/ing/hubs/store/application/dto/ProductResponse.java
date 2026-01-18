package com.ing.hubs.store.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ing.hubs.store.domain.entity.Product;
import lombok.Builder;

@Builder
public record ProductResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("price") Double price,
        @JsonProperty("quantity") Integer quantity
) {

    public static ProductResponse fromEntity(final Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }
}
