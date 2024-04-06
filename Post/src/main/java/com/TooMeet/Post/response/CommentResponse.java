package com.TooMeet.Post.response;

import com.TooMeet.Post.entity.CommentReaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
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
    private List<CommentReactionResponse> reactions = new ArrayList<>();//list reaction cai comment nay
    private int emoji=-1;//user
    private int replyCount = 0;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}
