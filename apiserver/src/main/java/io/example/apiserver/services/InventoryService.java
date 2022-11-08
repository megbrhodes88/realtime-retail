package io.example.apiserver.services;

import io.example.apiserver.components.InventorySubscriber;
import io.example.apiserver.events.ProductsInitializedEvent;
import io.example.apiserver.repository.InventoryRepository;
import io.example.apiserver.repository.ProductsRepository;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
public class InventoryService {
    Logger logger = Logger.getLogger(InventoryService.class);

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private KsqlService ksqlService;

    @EventListener(ProductsInitializedEvent.class)
    private void syncInventory() {
        String streamedQuery = "SELECT * FROM AGGREGATE_INVENTORY_DETAILS EMIT CHANGES;";

        try {
            ksqlService.streamQuery(streamedQuery)
                .thenAccept(streamedQueryResult -> {
                    logger.info("Query has started. Query ID: " + streamedQueryResult.queryID());
                    InventorySubscriber subscriber = new InventorySubscriber(productsRepository, inventoryRepository, template);
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