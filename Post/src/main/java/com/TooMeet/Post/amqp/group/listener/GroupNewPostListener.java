package com.TooMeet.Post.amqp.group.listener;

import com.TooMeet.Post.amqp.group.messsage.Choice;
import com.TooMeet.Post.amqp.group.messsage.PostMessage;
import com.TooMeet.Post.amqp.group.messsage.UpdatePostStatusMessage;
import com.TooMeet.Post.entity.Post;
import com.TooMeet.Post.repository.PostRepository;
import org.hibernate.sql.Update;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.channels.Pipe;

@Component
public class GroupNewPostListener {

    @Autowired
    PostRepository postRepository;

    @RabbitListener(queues = "q_post_new_post")
    public void receiveNewPostMessage(PostMessage message){
        Post post = new Post();
        post.setPrivacy(0);
        post.setAuthorId(message.getUserId());
        post.setStatus(Choice.pending.toString());
        post.setContent(message.getContent());
        if(message.getContent()==null) post.setContent("");
        post.setImages(message.getImages());
        post.setGroupId(message.getGroupId());
        System.out.println(message);
        postRepository.save(post);
    }

    @RabbitListener(queues = "q_post_accept_post")
    public void receiveUpdatePostStatus(UpdatePostStatusMessage message){

        Post post = postRepository.findById(message.getPostId()).orElse(null);
        if(post!= null) {
            if(!message.getGroupId().equals(post.getGroupId())) return;
            post.setStatus(Choice.accepted.toString());
            postRepository.save(post);
        }
    }

}
