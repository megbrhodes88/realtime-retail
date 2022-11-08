package io.example.apiserver.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.example.apiserver.models.Product;
import io.example.apiserver.repository.ProductsRepository;

@CrossOrigin
@RequestMapping("/api")
@RestController
public class ProductsController {
    
    @Autowired
    private ProductsRepository productsRepository;

    public ProductsController() {
        // TODO At the time of writing, I don't have a need for this constructor.
        // But I'm adding this comment to come back later.  
    }
    @GetMapping("/products")
    List<Product> getAllProducts() {
        return productsRepository.findAll();
    }

    @GetMapping("/products/{product}")
    Optional<Product> getProductById(@PathVariable String product) {
        return productsRepository.findById(product);
    }
}
