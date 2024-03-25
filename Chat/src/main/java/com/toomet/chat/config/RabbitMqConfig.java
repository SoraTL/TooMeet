package com.toomet.chat.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    // EXCHANGE
    @Value("${spring.rabbitmq.exchange.socket_exchange}")
    private String socketExchange;

    // QUEUE
    @Value("${spring.rabbitmq.queue.subscribe.chat_create_room}")
    private String chatCreateRoomQueue;

    @Value("${spring.rabbitmq.queue.public.socket_new_chat_message}")
    private String socketNewChatMessage;

    @Value("${spring.rabbitmq.queue.public.socket_update_chat_room}")
    private String socketUpdateChatRoom;

    @Value("${spring.rabbitmq.queue.public.socket_chat_room_member_ship}")
    private String socketChatRoomMemberShip;

    @Value("${spring.rabbitmq.queue.public.socket_chat_message_reaction}")
    private String socketChatMessageReaction;

    // ROUTING KEY
    @Value("${spring.rabbitmq.routing.socket_new_chat_message_routing}")
    private String socketNewChatMessageRoutingKey;

    @Value("${spring.rabbitmq.routing.socket_update_chat_room}")
    private String socketUpdateChatRoomRoutingKey;

    @Value("${spring.rabbitmq.routing.socket_chat_room_member_ship}")
    private String socketChatRoomMemberShipRoutingKey;

    @Value("${spring.rabbitmq.routing.socket_chat_message_reaction}")
    private String socketChatMessageReactionRoutingKey;

    /*  QUEUE   */
    @Bean
    public Queue chatCreateRoomQueue() {
        return new Queue(chatCreateRoomQueue);
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


    /* EXCHANGE */
    @Bean
    public TopicExchange socketExchange() {
        return new TopicExchange(socketExchange);
    }

    @Bean
    public Binding socketNewChatMessageBinding() {
        return BindingBuilder.bind(socketNewChatMessage())
                .to(socketExchange())
                .with(socketNewChatMessageRoutingKey);
    }

    @Bean
    public Binding socketUpdateChatRoomBinding() {
        return BindingBuilder.bind(socketUpdateChatRoom())
                .to(socketExchange())
                .with(socketUpdateChatRoomRoutingKey);
    }

    @Bean
    public Binding socketChatRoomMemberShipBinding() {
        return BindingBuilder.bind(socketChatRoomMemberShip())
                .to(socketExchange())
                .with(socketChatRoomMemberShipRoutingKey);
    }

    @Bean
    public Binding socketChatMessageReactionBinding() {
        return BindingBuilder.bind(socketChatMessageReaction())
                .to(socketExchange())
                .with(socketChatMessageReactionRoutingKey);
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
