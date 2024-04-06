package com.TooMeet.Post.amqp.group;

import com.TooMeet.Post.amqp.group.messsage.NewGroupPostMessage;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AMQPService {

    @Value("${spring.rabbitmq.queue.group_new_post_queue}")
    String groupNewPostQueue;
    @Value("${spring.rabbitmq.exchange.group_exchange}")
    String groupExchange;
    @Value("${spring.rabbitmq.routing.group_new_post_routing}")
    String groupNewPostRouting;

    private final AmqpTemplate amqpTemplate;
    @Autowired
    public AMQPService(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void sendNewPostMessage(NewGroupPostMessage message) {
        amqpTemplate.convertAndSend(groupExchange,groupNewPostRouting,message);
    }

}
