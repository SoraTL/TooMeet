package com.TooMeet.Post.repository;

import com.TooMeet.Post.amqp.group.messsage.Choice;
import com.TooMeet.Post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    @Override
    Page<Post> findAll(Pageable pageable);
    Post findOneByAuthorId(Long authorId);
    Page<Post> findByAuthorId(Long authorId, Pageable pageable);
    Page<Post> findByAuthorIdAndPrivacy(Long authorId, int privacy, Pageable pageable);
    Page<Post> findByGroupIdAndStatus(UUID groupId, Choice status, Pageable pageable);

}
