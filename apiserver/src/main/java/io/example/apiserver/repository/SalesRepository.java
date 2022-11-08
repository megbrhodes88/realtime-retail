package io.example.apiserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.example.apiserver.models.ProductSale;

@Repository
public interface SalesRepository extends JpaRepository<ProductSale, String> {
    // TODO Maybe someday something useful will go here. 
}
