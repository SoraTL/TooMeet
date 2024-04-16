package com.TooMeet.Post.amqp;

import com.TooMeet.Post.amqp.notification.message.CommentReactionMessage;
import com.TooMeet.Post.amqp.notification.message.NewCommentMessage;
import com.TooMeet.Post.amqp.notification.message.NewPostMessage;
import com.TooMeet.Post.amqp.notification.message.ReactionPostMessage;
import com.TooMeet.Post.amqp.socket.message.SocketCommentCountMessage;
import com.TooMeet.Post.amqp.socket.message.SocketNewCommentMessage;
import com.TooMeet.Post.amqp.socket.message.SocketReactionPostMessage;
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


    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    public AMQPService(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }



    public void sendNotifyNewPostMessage(NewPostMessage message){
        amqpTemplate.convertAndSend(notifyExchange,notifyNewPostRouting,message);
    }

    public void sendNotifyReactionMessage(ReactionPostMessage message) {
        amqpTemplate.convertAndSend(notifyExchange,notifyReactionRouting,message);
    }

    public void sendSocketReactionMessage(SocketReactionPostMessage message) {
        amqpTemplate.convertAndSend(socketExchange,socketReactionRouting,message);
    }

    public void sendSocketNewCommentMessage(SocketNewCommentMessage message) {
        amqpTemplate.convertAndSend(socketExchange,socketCreateCommentRouting,message);
    }

    public void sendSocketCommentCountMessage(SocketCommentCountMessage message) {
        amqpTemplate.convertAndSend(socketExchange,socketCommentCountRouting,message);
    }

    public void sendNotifyNewCommentMessage(NewCommentMessage message) {
        amqpTemplate.convertAndSend(notifyExchange,notifyNewCommentRouting,message);
    }

    public void sendNotifyCommentReactionMessage(CommentReactionMessage message) {
        amqpTemplate.convertAndSend(notifyExchange,notifyCommentReactionRouting,message);
    }

}
