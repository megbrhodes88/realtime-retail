package io.example.apiserver.components;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import io.example.apiserver.models.Product;
import io.example.apiserver.models.ProductInventory;
import io.example.apiserver.repository.InventoryRepository;
import io.example.apiserver.repository.ProductsRepository;

import io.confluent.ksql.api.client.Row;

@Service
public class InventorySubscriber implements Subscriber<Row>{
    private Logger logger = Logger.getLogger(InventorySubscriber.class);
    private ProductsRepository productsRepository;
    private InventoryRepository inventoryRepository;
    private SimpMessagingTemplate channel;
    private Subscription subscription;

    public InventorySubscriber(ProductsRepository productsRepository, InventoryRepository inventoryRepository, SimpMessagingTemplate channel) {
        this.productsRepository = productsRepository;
        this.inventoryRepository = inventoryRepository;
        this.channel = channel;
    }
    
    @Override
    public synchronized void onSubscribe(Subscription subscription) {
        logger.info("Successfully subscribed.");
        this.subscription = subscription;
        // request the fist row
        subscription.request(1);
    }
    @Override
    public synchronized void onNext(Row row) {
        logger.info("Received a row: "+ row.values());
        String product_id = row.values().getString(0);
        int product_available_inv = row.values().getInteger(1);
        int product_reserved_inv = row.values().getInteger(2);
        inventoryRepository.save(new ProductInventory(product_id, product_available_inv, product_reserved_inv));
        Optional<Product> optionalProduct = productsRepository.findById(product_id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setInventory(product_available_inv);
            productsRepository.save(product);
            channel.convertAndSend("/topic/products", product);
            channel.convertAndSend("/topic/products/"+product_id, product);
        } else {
            logger.error("Optional<Product> is false.");
        }
        subscription.request(1);
    }
    @Override
    public synchronized void onError(Throwable throwable) {
        logger.error("Received an error: " + throwable);
    }
    @Override
    public synchronized void onComplete() {
        logger.info("Query has ended.");
    }
}
