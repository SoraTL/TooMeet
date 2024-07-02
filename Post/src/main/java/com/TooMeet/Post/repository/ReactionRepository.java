package com.TooMeet.Post.repository;

import com.TooMeet.Post.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, UUID> {

    Reaction getByPostIdAndUserId(UUID postId,Long userId);

    void deleteReactionByPostIdAndUserId(UUID postId,Long userId);

}
