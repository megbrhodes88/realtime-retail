package io.example.apiserver.events;

import org.springframework.context.ApplicationEvent;

public class InventoryUpdateEvent extends ApplicationEvent {
    private String name;

    InventoryUpdateEvent(Object source, String name) {
        super(source);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
