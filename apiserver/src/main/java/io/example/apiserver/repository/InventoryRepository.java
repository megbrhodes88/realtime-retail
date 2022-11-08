package io.example.apiserver.repository;

import io.example.apiserver.models.ProductInventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<ProductInventory, String> {
    // TODO Maybe someday something useful will go here.     
}
