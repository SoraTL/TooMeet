//package com.TooMeet.Post.amqp;
//
//import org.springframework.amqp.core.AmqpTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AMQPService {
//
//    private final AmqpTemplate amqpTemplate;
//
//    @Autowired
//    public AMQPService(AmqpTemplate amqpTemplate) {
//        this.amqpTemplate = amqpTemplate;
//    }
//
//    public void sendNewPostMessage(String message) {
//        amqpTemplate.convertAndSend("exchange-name", "new-post-routing-key", message);
//    }
//
//    public void sendDeletePostMessage(String message) {
//        amqpTemplate.convertAndSend("exchange-name", "delete-post-routing-key", message);
//    }
//
//    public void sendNewCommentMessage(String message) {
//        amqpTemplate.convertAndSend("exchange-name", "new-comment-routing-key", message);
//    }
//
//    public void sendLikeMessage(String message) {
//        amqpTemplate.convertAndSend("exchange-name", "like-routing-key", message);
//    }
//}
