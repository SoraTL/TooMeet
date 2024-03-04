package com.TooMeet.Post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long userId;
    private UUID postId;
    private int type;
    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;

}
