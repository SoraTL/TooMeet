package com.TooMeet.Post.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class AMQPConfig {


    @Value("${spring.rabbitmq.queue.group_new_post_queue}")
    String groupNewPostQueue;
    @Value("${spring.rabbitmq.exchange.group_exchange}")
    String groupExchange;
    @Value("${spring.rabbitmq.routing.group_new_post_routing}")
    String groupNewPostRouting;

    @Value("${spring.rabbitmq.queue.notify_new_post_queue}")
    String notifyNewPostQueue;
    @Value("${spring.rabbitmq.exchange.notify_exchange}")
    String notifyExchange;
    @Value("${spring.rabbitmq.routing.notify_new_post_routing}")
    String notifyNewPostRouting;
    @Value("${spring.rabbitmq.queue.notify_reaction_post}")
    String notifyReactionQueue;
    @Value("${spring.rabbitmq.routing.notify_reaction_post}")
    String notifyReactionRouting;
    @Value("${spring.rabbitmq.queue.notify_new_comment_post}")
    String notifyNewCommentQueue;
    @Value("${spring.rabbitmq.routing.notify_new_comment_post}")
    String notifyNewCommentRouting;
    @Value("${spring.rabbitmq.queue.notify_comment_reaction_post}")
    String notifyCommentReactionQueue;
    @Value("${spring.rabbitmq.routing.notify_comment_reaction_post}")
    String notifyCommentReactionRouting;

    @Value("${spring.rabbitmq.exchange.socket_exchange}")
    String socketExchange;
    @Value("${spring.rabbitmq.queue.socket_reaction_post_queue}")
    String socketReactionQueue;
    @Value("${spring.rabbitmq.routing.socket_reaction_post_routing}")
    String socketReactionRouting;
    @Value("${spring.rabbitmq.queue.socket_create_comment_queue}")
    String socketCreateCommentQueue;
    @Value("${spring.rabbitmq.routing.socket_create_comment_routing}")
    String socketCreateCommentRouting;
    @Value("${spring.rabbitmq.queue.socket_comment_count_queue}")
    String socketCommentCountQueue;
    @Value("${spring.rabbitmq.routing.socket_comment_count_routing}")
    String socketCommentCountRouting;


    @Bean
    public TopicExchange groupExchange(){
        return new TopicExchange(groupExchange);
    }
    @Bean
    public TopicExchange notifyExchange(){
        return new TopicExchange(notifyExchange);
    }
    @Bean
    public TopicExchange socketExchange(){
        return new TopicExchange(socketExchange);
    }


    @Bean
    public Queue groupNewPostQueue(){
        return new Queue(groupNewPostQueue);
    }

    @Bean
    public Binding groupNewPostBinding(){
        return BindingBuilder.bind(groupNewPostQueue()).to(groupExchange()).with(groupNewPostRouting);
    }

    //New Post
    @Bean
    public Queue notifyNewPostQueue(){
        return new Queue(notifyNewPostQueue);
    }
    @Bean
    public Binding notifyNewPostBinding() {
        return BindingBuilder.bind(notifyNewPostQueue()).to(notifyExchange()).with(notifyNewPostRouting);
    }

    //Reaction Post
    @Bean
    public Queue notifyReactionQueue(){
        return new Queue(notifyReactionQueue);
    }
    @Bean
    public Binding notifyReactionBinding() {
        return BindingBuilder.bind(notifyReactionQueue()).to(notifyExchange()).with(notifyReactionRouting);
    }

    //Reaction Post
    @Bean
    public Queue notifyNewCommentQueue(){
        return new Queue(notifyNewCommentQueue);
    }
    @Bean
    public Binding notifyNewCommentBinding() {
        return BindingBuilder.bind(notifyNewCommentQueue()).to(notifyExchange()).with(notifyNewCommentRouting);
    }

    @Bean
    public Queue notifyCommentReactionQueue(){
        return new Queue(notifyCommentReactionQueue);
    }
    @Bean
    public Binding notifyCommentReactionBinding() {
        return BindingBuilder.bind(notifyCommentReactionQueue()).to(notifyExchange()).with(notifyCommentReactionRouting);
    }


    //Reaction Post Event(Socket)
    @Bean
    public Queue socketReactionQueue(){
        return new Queue(socketReactionQueue);
    }
    @Bean
    public Binding socketReactionBinding() {
        return BindingBuilder.bind(socketReactionQueue()).to(socketExchange()).with(socketReactionRouting);
    }

    //New Comment Event(Socket)
    @Bean
    public Queue socketNewCommentQueue(){
        return new Queue(socketCreateCommentQueue);
    }
    @Bean
    public Binding socketCreateCommentBinding() {
        return BindingBuilder.bind(socketNewCommentQueue()).to(socketExchange()).with(socketCreateCommentRouting);
    }

    //Count comment(Socket)
    @Bean
    public Queue socketCommentCountQueue(){
        return new Queue(socketCommentCountQueue);
    }
    @Bean
    public Binding socketCommentCountBinding() {
        return BindingBuilder.bind(socketCommentCountQueue()).to(socketExchange()).with(socketCreateCommentRouting);
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
