package com.TooMeet.Post.resposn;

import com.TooMeet.Post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private Header header;
    private List<Post> body;
    private int allPage;

    public static class Header {
    }

}
