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
@IdClass(ReactionId.class)
public class Reaction {

    @Id
    private Long userId;
    private int emoji = -1;
    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @Id
    private Post post;
    public void setPost(Post post) {
        this.post = post;
    }


}
