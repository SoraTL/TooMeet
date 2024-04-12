package com.toomeet.socket.friend;


import com.toomeet.socket.friend.event.FriendNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendNotificationListener {

    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "${spring.rabbitmq.queue.subscribe.socket_notify_friend_queue}")
    public void friendNotificationListener(FriendNotificationEvent event) {
        messagingTemplate.convertAndSend("/notifications/" + event.getReceiver().getId(), event);
    }


}
