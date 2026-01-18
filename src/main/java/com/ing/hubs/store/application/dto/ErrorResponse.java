package com.ing.hubs.store.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ErrorResponse(
        @JsonProperty("message") String message,
        @JsonProperty("http_code") Integer httpCode
) {
}
