package com.TooMeet.Post.service;

import com.TooMeet.Post.entity.Comment;
import com.TooMeet.Post.entity.CommentReaction;
import com.TooMeet.Post.repository.CommentReactionRepository;
import com.TooMeet.Post.repository.CommentRepository;
import com.TooMeet.Post.request.Format;
import com.TooMeet.Post.request.Image;
import com.TooMeet.Post.request.User;
import com.TooMeet.Post.response.AuthorDto;
import com.TooMeet.Post.response.CommentReactionResponse;
import com.TooMeet.Post.response.CommentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    @Value("${author.service.url}")
    private String authorServiceUrl;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CommentReactionRepository commentReactionRepository;

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

    public Page<CommentResponse> getComments(int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").ascending());
        Page<Comment> commentPage = commentRepository.findAll(pageable);
        return commentPage.map(comment -> convertToResponse(comment,userId));
    }

    public Page<CommentResponse> getComments(int page, int size,UUID postId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").ascending());
        Page<Comment> commentPage = commentRepository.findAll(pageable);
        return commentPage.map(this::convertToResponse);
    }

    public CommentResponse convertToResponse(Comment comment){
        CommentResponse commentResponse = new CommentResponse();

        String authorUrl = authorServiceUrl + "/users/info/" + comment.getUserId().toString();
        User author = restTemplate.getForObject(authorUrl, User.class);
//        User author =new User(2L,"asdfzc",new User.profile(new Image("asdzcxv", Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG));
        List<CommentReaction> reactions= commentReactionRepository.findByCommentId(comment.getId());
        List<CommentReactionResponse> responses = new ArrayList<>();
        for(CommentReaction reaction:reactions){
            CommentReactionResponse commentReaction = new CommentReactionResponse();
            commentReaction.setEmoji(reaction.getEmoji());
            commentReaction.setUser(new AuthorDto().convertToAuthor(restTemplate.getForObject(authorUrl, User.class)));
//            commentReaction.setUser(new AuthorDto().convertToAuthor(new User(2L,"asdfzc",new User.profile(new Image("asdzcxv", Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG))));
            responses.add(commentReaction);
        }
        CommentReaction userReaction =commentReactionRepository.getByCommentIdAndUserId(comment.getId(),author.getId());
        commentResponse.setId(comment.getId());
        CommentReaction commentReaction= new CommentReaction();
        return getCommentResponse(comment, commentResponse, author, responses, commentReaction,userReaction.getEmoji());

    }

    private CommentResponse getCommentResponse(Comment comment, CommentResponse commentResponse, User author, List<CommentReactionResponse> reactions, CommentReaction commentReaction,int emoji) {
        commentResponse.setReactions(reactions);
        commentResponse.setEmoji(emoji);
        commentResponse.setAuthor(new AuthorDto().convertToAuthor(author));
        commentResponse.setContent(comment.getContent());
        commentResponse.setParentId(comment.getParentId());
        commentResponse.setCreatedAt(comment.getCreatedAt());
        commentResponse.setUpdatedAt(comment.getUpdatedAt());
        commentResponse.setReplyCount(comment.getReplyCount());
        commentResponse.setLikeCount(comment.getLikeCount());
        commentResponse.setLevel(comment.getLevel());

        return commentResponse;
    }

    public CommentResponse convertToResponse(Comment comment,Long userId){
        CommentResponse commentResponse = new CommentResponse();

        String authorUrl = authorServiceUrl + "/users/info/" + comment.getUserId().toString();

        User author = restTemplate.getForObject(authorUrl, User.class);
//        User author =new User(2L,"asdfzc",new User.profile(new Image("asdzcxv", Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG));

        commentResponse.setId(comment.getId());
        List<CommentReaction> reactions= commentReactionRepository.findByCommentId(comment.getId());
        List<CommentReactionResponse> responses = new ArrayList<>();
        for(CommentReaction reaction:reactions){
            String userUrl = authorServiceUrl + "/users/info/" + reaction.getUserId();
            CommentReactionResponse commentReaction = new CommentReactionResponse();
            commentReaction.setEmoji(reaction.getEmoji());
            commentReaction.setUser(new AuthorDto().convertToAuthor(restTemplate.getForObject(userUrl, User.class)));
//            commentReaction.setUser(new AuthorDto().convertToAuthor(new User(2L,"asdfzc",new User.profile(new Image("asdzcxv", Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG))));
            responses.add(commentReaction);
        }

        CommentReaction userReaction =commentReactionRepository.getByCommentIdAndUserId(comment.getId(),userId);
        commentResponse.setId(comment.getId());
        CommentReaction commentReaction= new CommentReaction();
        if(userReaction==null){
            userReaction = new CommentReaction();
            userReaction.setUserId(userId);
            userReaction.setEmoji(-1);
        }



        return getCommentResponse(comment, commentResponse, author, responses, commentReaction,userReaction.getEmoji());
    }

    public Page<CommentResponse> getCommentsByParentId(UUID parentId, int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").ascending());
        Page<Comment> commentPage = commentRepository.findByParentId(parentId, pageable);
        return commentPage.map(comment -> convertToResponse(comment,userId));
    }

    public Page<CommentResponse> getCommentsByParentIdAndLevel(UUID parentId, int level, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findByParentIdAndLevel(parentId, level, pageable);

        return comments.map(comment -> {
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.setId(comment.getId());
            commentResponse.setContent(comment.getContent());
            commentResponse.setLevel(comment.getLevel());
            commentResponse.setLikeCount(comment.getLikeCount());
            commentResponse.setCreatedAt(comment.getCreatedAt());
            commentResponse.setUpdatedAt(comment.getUpdatedAt());
            commentResponse.setParentId(comment.getParentId());
            commentResponse.setReplyCount(comment.getReplyCount());
            return commentResponse;
        });
    }

    public boolean deleteCommentS(Comment comment){

        try{
            commentRepository.delete(comment);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

}