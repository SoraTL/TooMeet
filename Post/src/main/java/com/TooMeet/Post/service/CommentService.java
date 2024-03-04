package com.TooMeet.Post.service;

import com.TooMeet.Post.entity.Comment;
import com.TooMeet.Post.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;

    public Comment saveComment(Comment comment){

        if(comment.getId() == null){
            return commentRepository.save(comment);
        }
        else {
            return commentRepository.save(comment);
        }
    }

}
