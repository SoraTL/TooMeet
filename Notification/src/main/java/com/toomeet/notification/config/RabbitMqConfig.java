package com.toomeet.notification.config;

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

    //    QUEUE
    @Value("${spring.rabbitmq.queue.public.socket_notify_friend_queue}")
    private String socketNotifyFriendQueue;

    @Value("${spring.rabbitmq.queue.subscribe.notify_friend_request_queue}")
    private String notifyFriendRequestQueue;

    @Value("${spring.rabbitmq.queue.subscribe.notify_reply_friend_request_queue}")
    private String notifyReplyFriendRequestQueue;

    //    exchange
    @Value("${spring.rabbitmq.exchange.socket_exchange}")
    private String socketExchange;

    //    routing key
    @Value("${spring.rabbitmq.routing.socket_notify_friend_routing}")
    private String socketNotifyRoutingKey;

    @Bean
    public Queue socketNotifyFriendQueue() {
        return new Queue(socketNotifyFriendQueue);
    }

    @Bean
    public Queue notifyFriendRequestQueue() {
        return new Queue(notifyFriendRequestQueue);
    }

    @Bean
    public Queue notifyReplyFriendRequestQueue() {
        return new Queue(notifyReplyFriendRequestQueue);
    }

    @Bean
    public TopicExchange socketExchange() {
        return new TopicExchange(socketExchange);
    }

    @Bean
    public Binding notifyFriendBinding() {
        return BindingBuilder
                .bind(socketNotifyFriendQueue())
                .to(socketExchange())
                .with(socketNotifyRoutingKey);
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
