package io.example.apiserver.components;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import io.confluent.ksql.api.client.Row;
import io.example.apiserver.models.Product;
import io.example.apiserver.models.ProductSale;
import io.example.apiserver.repository.ProductsRepository;
import io.example.apiserver.repository.SalesRepository;

public class SalesSubscriber implements  Subscriber<Row> {
    Logger logger = Logger.getLogger(SalesSubscriber.class);
    private ProductsRepository productsRepository;
    private SalesRepository salesRepository;
    private SimpMessagingTemplate channel;
    private Subscription subscription;

    public SalesSubscriber(ProductsRepository productsRepository, SalesRepository salesRepository, SimpMessagingTemplate channel) {
        this.productsRepository = productsRepository;
        this.salesRepository = salesRepository;
        this.channel = channel;
    }

    @Override
    public synchronized void onSubscribe(Subscription subscription) {
        logger.info("Successfully subscribed.");
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public synchronized void onNext(Row row) {
        logger.info("Received a row: " + row.values());
        String product_id = row.values().getString(0);
        int discount = row.values().getInteger(1);
        salesRepository.save(new ProductSale(product_id, discount));
        Optional<Product> optionalProduct = productsRepository.findById(product_id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setDiscount(discount);
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
