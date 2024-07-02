package com.TooMeet.Post.response;

import com.TooMeet.Post.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
public class OriginPostResponse{

    private UUID id;
    private String content;
    private AuthorDto author;
    private int privacy;
    private List<String> images;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public OriginPostResponse convertToOriginPostResponse(Post post){
        setId(post.getId());
        setImages(post.getImages());
        setContent(post.getContent());
        setCreatedAt(post.getCreatedAt());
        setUpdatedAt(post.getUpdatedAt());
        setPrivacy(post.getPrivacy());
        return this;
    }

}
