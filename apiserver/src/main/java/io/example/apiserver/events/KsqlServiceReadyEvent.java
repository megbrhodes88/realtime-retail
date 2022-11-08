package io.example.apiserver.events;

import org.springframework.context.ApplicationEvent;

public class KsqlServiceReadyEvent extends ApplicationEvent {
    private String name;

    KsqlServiceReadyEvent(Object source, String name) {
        super(source);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
