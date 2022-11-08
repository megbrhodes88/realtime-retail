package io.example.apiserver.events;

import org.springframework.context.ApplicationEvent;

public class ProductsInitializedEvent extends ApplicationEvent {
    private String name;

    ProductsInitializedEvent(Object source, String name) {
        super(source);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
