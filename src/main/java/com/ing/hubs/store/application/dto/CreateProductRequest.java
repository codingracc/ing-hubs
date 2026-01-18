package com.ing.hubs.store.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ing.hubs.store.domain.entity.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
        @JsonProperty("name") @NotBlank String name,
        @JsonProperty("description") String description,
        @JsonProperty("price") @NotNull @Min(0) Double price,
        @JsonProperty("quantity") @NotNull @Min(0) Integer quantity
) {

    public Product toEntity() {
        return Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .quantity(quantity)
                .build();
    }
}