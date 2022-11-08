// package com.example.realtimestorefrontcc.configs;

// import org.springframework.stereotype.Component;
// import org.springframework.web.socket.CloseStatus;
// import org.springframework.web.socket.WebSocketHandler;
// import org.springframework.web.socket.WebSocketMessage;
// import org.springframework.web.socket.WebSocketSession;

// TODO This clearly isn't necessary, but I'm to afraid to delete it.
// Maybe this will have some use int he future. 

// @Component
// public class WebSocketHandlerConfig implements WebSocketHandler {
//     @Override
//     public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
//         System.out.println("after connection closed");
//     }
//     @Override
//     public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//         System.out.println("after connection established");
        
//     }
//     @Override
//     public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//         System.out.println("handle message");
        
//     }
//     @Override
//     public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//         System.out.println("transport error");
        
//     }
//     @Override
//     public boolean supportsPartialMessages() {
//         return false;
//     }
    
// }
