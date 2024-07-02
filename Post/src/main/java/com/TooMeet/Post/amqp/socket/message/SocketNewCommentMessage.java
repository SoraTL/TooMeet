package com.TooMeet.Post.amqp.socket.message;

import com.TooMeet.Post.response.AuthorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SocketNewCommentMessage {

    private UUID postId;
    private CommentResponseForSocket comment;
    private Date timestamp;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentResponseForSocket {

        private UUID commentId;
        private String content;
        private AuthorDto author;

    }

}
