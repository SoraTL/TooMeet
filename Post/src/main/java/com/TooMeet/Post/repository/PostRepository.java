package com.TooMeet.Post.repository;

import com.TooMeet.Post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByAuthorId(Long authorId);
}
