package com.TooMeet.Post.service;

import com.TooMeet.Post.entity.CommentReaction;
import com.TooMeet.Post.repository.CommentReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentReactionService {

    @Autowired
    CommentReactionRepository commentReactionRepository;

    public CommentReaction reaction(CommentReaction reaction){
        return commentReactionRepository.save(reaction);
    }

}
