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
public class NewPostMessage {

    private UUID id;
    private AuthorDto author;
    private Type type;


    public enum Type {
        NEW,
        SHARE
    }

    public Date timestamp;


}
