package com.ing.hubs.store.application.controller;

import com.ing.hubs.store.application.dto.CreateProductRequest;
import com.ing.hubs.store.application.dto.ProductResponse;
import com.ing.hubs.store.domain.entity.Product;
import com.ing.hubs.store.domain.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product entityToCreate = request.toEntity();
        Product entityCreated = productService.createProduct(entityToCreate);
        return ResponseEntity.ok(ProductResponse.fromEntity(entityCreated));
    }


}
