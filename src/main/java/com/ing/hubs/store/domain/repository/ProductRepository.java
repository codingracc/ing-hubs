package com.ing.hubs.store.domain.repository;

import com.ing.hubs.store.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}