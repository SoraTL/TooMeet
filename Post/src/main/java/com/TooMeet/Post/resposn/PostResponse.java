package com.TooMeet.Post.resposn;

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
    private String avatar;
    private AuthorDto author = new AuthorDto();
    private String name;
    private String content;
    private List<String> imageUrl;
    private int privacy;
    private int emoji;
    private int reactionCount;
    private int commentCount;
    private Timestamp createAt;
    private Timestamp updateAt;

}
