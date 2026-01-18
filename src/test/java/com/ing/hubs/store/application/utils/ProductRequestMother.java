package com.ing.hubs.store.application.utils;

import com.ing.hubs.store.application.dto.CreateProductRequest;
import com.ing.hubs.store.application.dto.UpdateProductPriceRequest;
import com.ing.hubs.store.application.dto.UpdateProductQuantityRequest;

public final class ProductRequestMother {

    private ProductRequestMother() {
    }

    public static CreateProductRequest aCreateProductRequest(final String name) {
        return new CreateProductRequest(name, "Fresh milk", 5.5, 10);
    }

    public static UpdateProductPriceRequest anUpdatePriceRequest(final double price) {
        return new UpdateProductPriceRequest(price);
    }

    public static UpdateProductQuantityRequest anUpdateQuantityRequest(final int quantity) {
        return new UpdateProductQuantityRequest(quantity);
    }
}
