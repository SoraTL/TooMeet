package com.TooMeet.Post.repository;

import com.TooMeet.Post.entity.Comment;
import com.TooMeet.Post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Comment findByParentId(UUID parentId);
    @Override
    Page<Comment> findAll(Pageable pageable);

    Page<Comment> findByParentId(UUID parentId, Pageable pageable);
}
