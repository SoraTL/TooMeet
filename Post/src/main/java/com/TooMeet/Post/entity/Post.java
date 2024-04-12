package com.TooMeet.Post.entity;

import com.TooMeet.Post.amqp.group.messsage.Choice;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long authorId;
    @Column(length = 5000)
    private String content;
    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;
    private List<String> images;
    private int privacy;
    private int reactionCount = 0;
    private int commentCount = 0;
    private UUID groupId;
    private Choice status;

    @ManyToOne
    @JoinColumn
    private Post originPost;
    @OneToMany(mappedBy = "originPost",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Post> sharedPosts = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Reaction> reactions = new ArrayList<>();


}


