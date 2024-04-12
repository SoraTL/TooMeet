package com.toomeet.socket.friend;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class FriendController {
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping({"push-notify",})
    public void test(@Payload String message, Principal userId) {

        messagingTemplate.convertAndSend("/notifications/" + userId, message);
    }

}
