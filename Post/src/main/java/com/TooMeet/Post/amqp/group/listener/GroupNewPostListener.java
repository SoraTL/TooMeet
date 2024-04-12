package com.TooMeet.Post.amqp.group.listener;

import com.TooMeet.Post.amqp.group.messsage.Choice;
import com.TooMeet.Post.amqp.group.messsage.NewGroupPostMessage;
import com.TooMeet.Post.entity.Post;
import com.TooMeet.Post.repository.PostRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GroupNewPostListener {

    @Autowired
    PostRepository postRepository;

    @RabbitListener(queues = "q_post_new_post")
    public void receiveNewPostMessage(NewGroupPostMessage message){

        Post post = new Post();
        post.setPrivacy(0);
        post.setStatus(Choice.pending);
        post.setContent(message.getContent());
        if(message.getContent()==null) post.setContent("");
        post.setImages(message.getImages());
        post.setGroupId(message.getGroupId());
        postRepository.save(post);
        System.out.println("received");

    }


}
