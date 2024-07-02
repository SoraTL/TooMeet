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
public class CommentReactionMessage {

    private UUID postId;
    private UUID commentId;
    private int emoji;
    private AuthorDto commentator;
    private AuthorDto author;
    private Date timestamp;

}
