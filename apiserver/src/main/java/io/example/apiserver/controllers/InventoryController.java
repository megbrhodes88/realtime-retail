package io.example.apiserver.controllers;

import java.util.List;
import java.util.Optional;

import io.example.apiserver.models.ProductInventory;
import io.example.apiserver.repository.InventoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RequestMapping("/api")
@RestController
public class InventoryController {

    @Autowired
    private InventoryRepository repository;

    public InventoryController() {
        // TODO At the time of writing, I don't have a need for this constructor.
        // But I'm adding this comment to come back later.       
    }
    @GetMapping("/inventory")
    List<ProductInventory> getAllInventory() {
        return repository.findAll();
    }

    @GetMapping("/inventory/{product}")
    Optional<ProductInventory> getProductInventory(@PathVariable String product) {
        return repository.findById(product);
    }

    @MessageMapping("/inventory/reserve")
    public void markInventoryReserved(String test) {
        System.out.println(test);
    }
}
