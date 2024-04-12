package com.toomeet.user.config;


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
    // exchange
    @Value("${spring.rabbitmq.exchange.notify_exchange}")
    private String notifyExchange;

    @Value("${spring.rabbitmq.exchange.chat_exchange}")
    private String chatExchange;

    // queue
    @Value("${spring.rabbitmq.queue.public.notify_friend_request}")
    private String notifyFriendRequestQueue;

    @Value("${spring.rabbitmq.queue.public.notify_reply_friend_request}")
    private String notifyReplyFriendRequestQueue;

    @Value("${spring.rabbitmq.queue.public.chat_create_room}")
    private String chatCreateRoomQueue;

    // routing key
    @Value("${spring.rabbitmq.routing.notify_friend_request}")
    private String notifyFriendRequestRoutingKey;

    @Value("${spring.rabbitmq.routing.notify_reply_friend_request}")
    private String notifyReplyFriendRequestRoutingKey;

    @Value("${spring.rabbitmq.routing.chat_create_room}")
    private String createChatRoomRoutingKey;

    /*  QUEUE   */
    @Bean
    public Queue notifyFriendRequestQueue() {
        return new Queue(notifyFriendRequestQueue);
    }

    @Bean
    public Queue notifyReplyFriendRequestQueue() {
        return new Queue(notifyReplyFriendRequestQueue);
    }

    @Bean
    public Queue chatCreateRoomQueue() {
        return new Queue(chatCreateRoomQueue);
    }

    /*  EXCHANGE   */
    @Bean
    public TopicExchange notifyExchange() {
        return new TopicExchange(notifyExchange);
    }

    @Bean
    TopicExchange chatExchange() {
        return new TopicExchange(chatExchange);
    }

    /*  BINDING   */

    @Bean
    public Binding friendRequestBinding() {
        return BindingBuilder
                .bind(notifyFriendRequestQueue())
                .to(notifyExchange())
                .with(notifyFriendRequestRoutingKey);
    }

    @Bean
    public Binding friendReplyBinding() {
        return BindingBuilder
                .bind(notifyReplyFriendRequestQueue())
                .to(notifyExchange())
                .with(notifyReplyFriendRequestRoutingKey);
    }

    @Bean
    public Binding createCharRoomBinding() {
        return BindingBuilder.bind(chatCreateRoomQueue())
                .to(chatExchange())
                .with(createChatRoomRoutingKey);
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

