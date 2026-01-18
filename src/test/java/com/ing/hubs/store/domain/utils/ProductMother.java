package com.ing.hubs.store.domain.utils;


import com.ing.hubs.store.domain.entity.Product;

public final class ProductMother {

    private ProductMother() {
    }

    private static Product.ProductBuilder aProduct() {
        return Product.builder()
                .name("Milk")
                .description("Fresh milk")
                .price(5.5)
                .quantity(10);
    }

    public static Product aProductEntity() {
        return aProduct().build();
    }

    public static Product aProductEntity(final Long id) {
        return aProduct().id(id).build();
    }

    public static Product aProductEntity(final Long id, final String name) {
        return aProduct().id(id).name(name).build();
    }
}
