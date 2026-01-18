package com.ing.hubs.store.application.controller;

import com.ing.hubs.store.application.dto.CreateProductRequest;
import com.ing.hubs.store.application.dto.ProductResponse;
import com.ing.hubs.store.application.dto.UpdateProductPriceRequest;
import com.ing.hubs.store.application.dto.UpdateProductQuantityRequest;
import com.ing.hubs.store.domain.entity.Product;
import com.ing.hubs.store.domain.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product entityToCreate = request.toEntity();
        Product entityCreated = productService.createProduct(entityToCreate);
        return ResponseEntity.status(CREATED).body(ProductResponse.fromEntity(entityCreated));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts()
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable @NotNull Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ProductResponse.fromEntity(product));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/by-name/{name}")
    public ResponseEntity<ProductResponse> getProductByName(@PathVariable @NotBlank String name) {
        Product product = productService.getProductByName(name);
        return ResponseEntity.ok(ProductResponse.fromEntity(product));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<Void> deleteAllProducts() {
        productService.deleteAllProducts();
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable @NotNull Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/by-name/{name}")
    public ResponseEntity<Void> deleteProductByName(@PathVariable @NotBlank String name) {
        productService.deleteProductByName(name);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/price")
    public ResponseEntity<ProductResponse> updateProductPrice(
            @PathVariable @NotNull Long id,
            @Valid @RequestBody UpdateProductPriceRequest request
    ) {
        Product updated = productService.updateProductPrice(id, request.price());
        return ResponseEntity.ok(ProductResponse.fromEntity(updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/quantity")
    public ResponseEntity<ProductResponse> updateProductQuantity(
            @PathVariable @NotNull Long id,
            @Valid @RequestBody UpdateProductQuantityRequest request
    ) {
        Product updated = productService.updateProductQuantity(id, request.quantity());
        return ResponseEntity.ok(ProductResponse.fromEntity(updated));
    }
}
