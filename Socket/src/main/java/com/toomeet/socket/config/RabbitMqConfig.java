package com.toomeet.socket.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    // QUEUE
    @Value("${spring.rabbitmq.queue.subscribe.socket_notify_friend_queue}")
    private String socketNotifyFriendQueue;

    @Value("${spring.rabbitmq.queue.subscribe.socket_new_chat_message}")
    private String socketNewChatMessage;

    @Value("${spring.rabbitmq.queue.subscribe.socket_update_chat_room}")
    private String socketUpdateChatRoom;

    @Value("${spring.rabbitmq.queue.subscribe.socket_chat_room_member_ship}")
    private String socketChatRoomMemberShip;

    @Value("${spring.rabbitmq.queue.subscribe.socket_chat_message_reaction}")
    private String socketChatMessageReaction;

    @Bean
    public Queue socketNotifyFriendQueue() {
        return new Queue(socketNotifyFriendQueue);
    }

    @Bean
    public Queue socketNewChatMessage() {
        return new Queue(socketNewChatMessage);
    }

    @Bean
    public Queue socketUpdateChatRoom() {
        return new Queue(socketUpdateChatRoom);
    }

    @Bean
    public Queue socketChatRoomMemberShip() {
        return new Queue(socketChatRoomMemberShip);
    }

    @Bean
    public Queue socketChatMessageReaction() {
        return new Queue(socketChatMessageReaction);
    }

    @Bean
    public AmqpTemplate amqpTemplate(final ConnectionFactory factory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(factory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
