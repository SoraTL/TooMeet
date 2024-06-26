package com.TooMeet.Post.response;

import com.TooMeet.Post.amqp.group.messsage.Choice;
import com.TooMeet.Post.entity.Post;
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
public class GroupPostResponse{

    private UUID id;
    private AuthorDto author = new AuthorDto();
    private String content;
    private List<String> images;
    private int privacy;
    private int emoji = -1;
    private int reactionCount;
    private int commentCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Choice status;

    public void convertToResponse(Post post){
        this.setId(post.getId());
        this.setContent(post.getContent());
        this.setImages(post.getImages());
        this.setPrivacy(post.getPrivacy());
        this.setReactionCount(post.getReactionCount());
        this.setCommentCount(post.getCommentCount());
        this.setCreatedAt(post.getCreatedAt());
        this.setUpdatedAt(post.getUpdatedAt());
    }

}
