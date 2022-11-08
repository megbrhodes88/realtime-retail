package io.example.apiserver.services;

import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import io.example.apiserver.events.CustomEventPublisher;
import io.example.apiserver.events.KsqlServiceReadyEvent;
import io.example.apiserver.models.Product;
import io.example.apiserver.models.ProductInventory;
import io.example.apiserver.models.ProductSale;
import io.example.apiserver.repository.InventoryRepository;
import io.example.apiserver.repository.ProductsRepository;
import io.example.apiserver.repository.SalesRepository;
import io.confluent.ksql.api.client.Row;

@Service
public class ProductsService {
    Logger logger = Logger.getLogger(ProductsService.class);

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired 
    private InventoryRepository inventoryRepository;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private CustomEventPublisher publisher;

    @Autowired
    private KsqlService ksqlService;

    String productsPullQuery = "SELECT * FROM PRODUCTS;";
    String inventoryPullQUery = "SELECT * FROM AGGREGATE_INVENTORY_DETAILS;";
    String salesPullQuery = "SELECT * FROM SALES;";
    Timer timer;

    @EventListener(KsqlServiceReadyEvent.class)
    private void initProducts() {
        try {
            updateProducts();
            updateInventory();
            updateSales();
            publisher.publishEvent("ProductsInitialized");
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (ExecutionException ee) {
            ee.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventListener(KsqlServiceReadyEvent.class)
    private void syncProducts() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    updateInventory();
                    updateSales(); 
                    updateProducts();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                } catch (ExecutionException ee) {
                    ee.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 2500, 5000);
    }

    private void updateProducts() throws InterruptedException, ExecutionException {
        List<Row> productsResultRows = ksqlService.pullQuery(productsPullQuery);
        for (Row row : productsResultRows) {
            // TODO THERE MUST BE A WAY TO CREATE THIS INTO POJO
            String product_id = row.values().getString(0);
            String car_year = row.values().getString(1);
            String car_make = row.values().getString(2);
            String car_model = row.values().getString(3);
            String product_name = row.values().getString(4);
            int product_price = row.values().getInteger(5);
            String product_subtitle = row.values().getString(6);
            String product_desc = row.values().getString(7);
            int inventory = 0;
            int discount = 0;
            Optional<ProductInventory> optionalInventory = inventoryRepository.findById(product_id);
            if (optionalInventory.isPresent()) {
                ProductInventory productInventory = optionalInventory.get();
                inventory = productInventory.getProductInvOnHand();
            }
            Optional<ProductSale> optionalSale = salesRepository.findById(product_id);
            if (optionalSale.isPresent()) {
                ProductSale productSale = optionalSale.get();
                discount = productSale.getDiscount();
            }
            productsRepository.save(new Product(product_id, car_year, car_make, car_model, product_name, product_price, product_subtitle, product_desc, inventory, discount));
        }
    }
    
    private void updateInventory() throws InterruptedException, ExecutionException {
        List<Row> inventoryResultRows = ksqlService.pullQuery(inventoryPullQUery);
        for (Row row : inventoryResultRows) {
            String product_id = row.values().getString(0);
            int product_available_inv = row.values().getInteger(1);
            int product_reserved_inv = row.values().getInteger(2);
            inventoryRepository.save(new ProductInventory(product_id, product_available_inv, product_reserved_inv));
            Optional<Product> optionalProduct = productsRepository.findById(product_id);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                product.setInventory(product_available_inv);
                productsRepository.save(product);
            } else {
                logger.error("Optional<Product> is false.");
            }
        }
    }

    private void updateSales() throws InterruptedException, ExecutionException {
        List<Row> salesResultRows = ksqlService.pullQuery(salesPullQuery);
        for (Row row : salesResultRows) {
            String product_id = row.values().getString(0);
            int discount = row.values().getInteger(1);
            salesRepository.save(new ProductSale(product_id, discount));
            Optional<Product> optionalProduct = productsRepository.findById(product_id);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                product.setDiscount(discount);
                productsRepository.save(product);
            } else {
                logger.error("Optional<Product> is false.");
            }
        }
    }
    @PreDestroy
    private void closeTimer() {
        timer.cancel();
    }
}
