package com.TooMeet.Post.amqp.group.messsage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdatePostStatusMessage {

    private UUID postId;
    private UUID groupId;

}
