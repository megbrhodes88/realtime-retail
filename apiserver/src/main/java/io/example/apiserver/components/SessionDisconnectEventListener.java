package io.example.apiserver.components;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class SessionDisconnectEventListener implements ApplicationListener<SessionDisconnectEvent> {
    private Logger logger = Logger.getLogger(SessionDisconnectEventListener.class);

    @Autowired
    SimpMessagingTemplate template;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        logger.info("Session disconnected: " + sessionId);
    }
}
