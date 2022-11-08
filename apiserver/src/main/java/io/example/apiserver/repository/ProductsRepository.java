package io.example.apiserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.example.apiserver.models.Product;

@Repository
public interface ProductsRepository extends JpaRepository<Product, String> {
    
}
