package com.TooMeet.Post.repository;

import com.TooMeet.Post.entity.Post;
import com.TooMeet.Post.resposn.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByAuthorId(Long authorId);
    @Override
    Page<Post> findAll(Pageable pageable);
}
