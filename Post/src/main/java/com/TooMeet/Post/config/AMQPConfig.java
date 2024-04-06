package com.TooMeet.Post.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AMQPConfig {


    @Value("${spring.rabbitmq.queue.group_new_post_queue}")
    String groupNewPostQueue;
    @Value("${spring.rabbitmq.exchange.group_exchange}")
    String groupExchange;
    @Value("${spring.rabbitmq.routing.group_new_post_routing}")
    String groupNewPostRouting;

    @Bean
    public TopicExchange groupExchange(){
        return new TopicExchange(groupExchange);
    }

    @Bean
    public Queue groupNewPostQueue(){
        return new Queue(groupNewPostQueue);
    }

    @Bean
    public Binding groupNewPostBinding(){
        return BindingBuilder.bind(groupNewPostQueue()).to(groupExchange()).with(groupNewPostRouting);
    }

}
