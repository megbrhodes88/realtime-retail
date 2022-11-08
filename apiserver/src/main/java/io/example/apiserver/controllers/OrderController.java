package io.example.apiserver.controllers;

import io.example.apiserver.models.Order;
import io.example.apiserver.services.ProducerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RequestMapping("/api")
@RestController
public class OrderController {
    String ORDERS_TOPIC_NAME = "orders";

    @Autowired
    private ProducerService producerService;

    public OrderController() {
        // TODO At the time of writing, I don't have a need for this constructor.
        // But I'm adding this comment to come back later.
    }

    @PostMapping("/orders")
    Order createNewOrder(@RequestBody Order order) throws Exception {
        producerService.send(order, this.ORDERS_TOPIC_NAME);
        return order;
    }
}
