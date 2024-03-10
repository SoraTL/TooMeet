package com.TooMeet.Post.service;

import com.TooMeet.Post.entity.Comment;
import com.TooMeet.Post.repository.CommentRepository;
import com.TooMeet.Post.resposn.CommentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

    public Comment getCommentByParentId(UUID parentId){
        return commentRepository.findByParentId(parentId);
    }

    public Page<CommentResponse> getComments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<Comment> commentPage = commentRepository.findAll(pageable);
        return commentPage.map(this::convertToResponse);
    }

    public CommentResponse convertToResponse(Comment comment){
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setLiked(false);
        commentResponse.setCreatedAt(comment.getCreatedAt());
        commentResponse.setUpdatedAt(comment.getUpdatedAt());
        commentResponse.setLikeCount(0);
        return commentResponse;
    }

    public Page<CommentResponse> getCommentsByParentId(UUID parentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<Comment> commentPage = commentRepository.findByParentId(parentId, pageable);
        return commentPage.map(this::convertToResponse);
    }
}
