package com.TooMeet.Post.resposn;

import com.TooMeet.Post.request.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private UUID id;
    private String content;
    private int level;
    private UUID parentId;
    private AuthorDto author = new AuthorDto();
    private int likeCount;
    private CommentReactionResponse reaction = new CommentReactionResponse();
    private int replyCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
