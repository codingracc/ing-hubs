package com.ing.hubs.store.domain.service;

import com.ing.hubs.store.domain.entity.Product;
import com.ing.hubs.store.domain.exception.Conflict;
import com.ing.hubs.store.domain.exception.NotFound;
import com.ing.hubs.store.domain.repository.ProductRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repository;

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Product getProductById(final @NotNull Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFound("Product not found with id: " + id));
    }

    public Product getProductByName(final @NotBlank String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new NotFound("Product not found with name: " + name));
    }

    @Transactional
    public void deleteAllProducts() {
        repository.deleteAll();
    }

    @Transactional
    public void deleteProductById(final @NotNull Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        }
    }

    @Transactional
    public void deleteProductByName(final @NotBlank String name) {
        if (repository.existsByName(name)) {
            repository.deleteByName(name);
        }
    }

    @Transactional
    public Product createProduct(final @NotNull @Valid Product product) {
        if (repository.existsByName(product.getName())) {
            throw new Conflict("Product already exists with name: " + product.getName());
        }
        return repository.save(product);
    }

    @Transactional
    public Product updateProductPrice(
            final @NotNull Long id,
            final @NotNull @Min(0) Double newPrice
    ) {
        final Product product = getProductById(id);
        return repository.save(product.withPrice(newPrice));
    }

    @Transactional
    public Product updateProductQuantity(
            final @NotNull Long id,
            final @NotNull @Min(0) Integer newQuantity
    ) {
        final Product product = getProductById(id);
        return repository.save(product.withQuantity(newQuantity));
    }
}
