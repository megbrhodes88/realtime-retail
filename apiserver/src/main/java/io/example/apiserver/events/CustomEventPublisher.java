package io.example.apiserver.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class CustomEventPublisher {
    private final ApplicationEventPublisher publisher;
    CustomEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }
    public void publishEvent(final String name) {
        
        switch (name) {
            case "KsqlServiceReady":
                publisher.publishEvent(new KsqlServiceReadyEvent(this, name));
                break;
            case "ProductsInitialized":
                publisher.publishEvent(new ProductsInitializedEvent(this, name));
            case "InventoryUpdate":
                publisher.publishEvent(new InventoryUpdateEvent(this, name));
                break;
        }
    }
}
