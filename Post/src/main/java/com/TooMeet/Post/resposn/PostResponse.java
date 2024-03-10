package com.TooMeet.Post.resposn;

import com.TooMeet.Post.entity.Post;
import lombok.*;
import org.springframework.http.StreamingHttpOutputMessage;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private UUID id;
    private AuthorDto author = new AuthorDto();
    private String content;
    private List<String> images;
    private int privacy;
    private int emoji = -1;
    private int reactionCount;
    private int commentCount;
    private Timestamp createAt;
    private Timestamp updateAt;

    public void convertToResponse(Post post){
        this.setId(post.getId());
        this.setContent(post.getContent());
        this.setImages(post.getImages());
        this.setPrivacy(post.getPrivacy());
        this.setReactionCount(post.getReactionCount());
        this.setCommentCount(post.getCommentCount());
        this.setCreateAt(post.getCreatedAt());
        this.setUpdateAt(post.getUpdatedAt());
    }

}
