package com.TooMeet.Post.resposn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private UUID id;
    private String content;
    private int level;
    private AuthorDto author;
    private int likeCount;
    private boolean liked;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
