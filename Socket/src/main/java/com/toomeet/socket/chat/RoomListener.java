package com.toomeet.socket.chat;

import com.toomeet.socket.chat.event.MemberShipEvent;
import com.toomeet.socket.chat.event.UpdateChatRoomEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomListener {
    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "${spring.rabbitmq.queue.subscribe.socket_update_chat_room}")
    public void roomUpdateListener(UpdateChatRoomEvent event) {
        messagingTemplate.convertAndSend("/chat-room/" + event.getRoomId(), event);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue.subscribe.socket_chat_room_member_ship}")
    public void roomMemberShipListener(MemberShipEvent event) {
        messagingTemplate.convertAndSend("/chat-members/" + event.getRoomId(), event);
    }
}
