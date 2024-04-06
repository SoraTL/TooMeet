package com.TooMeet.Post.amqp.group.messsage;

import com.TooMeet.Post.request.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewGroupPostMessage {

    private UUID postId;
    private User postAuthor;

}
