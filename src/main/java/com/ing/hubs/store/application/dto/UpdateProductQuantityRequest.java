package com.ing.hubs.store.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateProductQuantityRequest(
        @JsonProperty("quantity") @NotNull @Min(0) Integer quantity
) {
}