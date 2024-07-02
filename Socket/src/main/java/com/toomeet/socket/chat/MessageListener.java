package com.toomeet.socket.chat;

import com.toomeet.socket.chat.event.MessageReactionEvent;
import com.toomeet.socket.chat.event.NewMessageEvent;
import com.toomeet.socket.chat.event.RecallMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageListener {
    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "${spring.rabbitmq.queue.subscribe.socket_new_chat_message}")
    public void newChatMessageListener(NewMessageEvent event) {
        messagingTemplate.convertAndSend("/messages/" + event.getRoomId(), event);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue.subscribe.socket_chat_message_reaction}")
    public void messageReactionListener(MessageReactionEvent event) {
        messagingTemplate.convertAndSend("/message-reaction/" + event.getMessageId(), event);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue.subscribe.socket_chat_message_recall}")
    public void messageRecallListener(RecallMessageEvent event) {
        messagingTemplate.convertAndSend("/message-recall/" + event.getId(), event);
    }


}
