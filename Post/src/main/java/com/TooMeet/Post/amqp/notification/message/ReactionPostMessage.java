package com.TooMeet.Post.amqp.notification.message;

import com.TooMeet.Post.response.AuthorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReactionPostMessage {
    private UUID postId;
    private AuthorDto author;
    private AuthorDto sender;
    private int emoji;
    private Date timestamp;
}
