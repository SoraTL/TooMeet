package com.TooMeet.Post.amqp.notification.message;

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
public class NewCommentMessage {
    private UUID postId;

    private AuthorDto sender;
    private AuthorDto author;
    private UUID commentId;
    private Date timestamp;
}
