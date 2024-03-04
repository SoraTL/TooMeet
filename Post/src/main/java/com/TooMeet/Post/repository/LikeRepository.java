package com.TooMeet.Post.repository;

import com.TooMeet.Post.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LikeRepository extends JpaRepository<Reaction, UUID> {
}
