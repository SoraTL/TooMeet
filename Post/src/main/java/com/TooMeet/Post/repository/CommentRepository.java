package com.TooMeet.Post.repository;

import com.TooMeet.Post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Comment findByParentId(UUID parentId);
}
