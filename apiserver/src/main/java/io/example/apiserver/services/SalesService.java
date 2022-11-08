package io.example.apiserver.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import io.example.apiserver.components.SalesSubscriber;
import io.example.apiserver.events.ProductsInitializedEvent;
import io.example.apiserver.repository.ProductsRepository;
import io.example.apiserver.repository.SalesRepository;

@Service
public class SalesService {
    private Logger logger = Logger.getLogger(SalesService.class);

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private KsqlService ksqlService;

    @EventListener(ProductsInitializedEvent.class)
    private void syncSales() {
        // TODO Need to add some kind of retry loop.
        String streamedQuery = "SELECT * FROM SALES EMIT CHANGES;";
        try {
            ksqlService.streamQuery(streamedQuery)
                .thenAccept(streamedQueryResult -> {
                    logger.info("Query started with id: " + streamedQueryResult.queryID());
                    SalesSubscriber subscriber = new SalesSubscriber(productsRepository, salesRepository, template);
                    streamedQueryResult.subscribe(subscriber);
                }).exceptionally(error -> {
                    logger.error("Request failed: " + error);
                    return null;
                });
        } catch (Exception e) {
            logger.error("Exception: ");
            e.printStackTrace();
        }
    }
}
