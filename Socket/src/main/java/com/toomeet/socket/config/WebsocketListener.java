package com.toomeet.socket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebsocketListener {
    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener()
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("A user connection");
        System.out.println("event = " + event);



    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        System.out.println("A user offline");
        System.out.println("event = " + event);

    }
}
