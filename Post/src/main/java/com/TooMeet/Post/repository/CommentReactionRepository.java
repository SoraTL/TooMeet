package com.TooMeet.Post.repository;

import com.TooMeet.Post.entity.CommentReaction;
import com.TooMeet.Post.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, UUID> {

    CommentReaction getByCommentIdAndUserId(UUID commentId, Long userId);

    void deleteReactionByCommentIdAndUserId(UUID commentId,Long userId);

    List<CommentReaction> findByCommentId(UUID commentId);

}
