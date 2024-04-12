package com.TooMeet.Post.amqp.socket.message;

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
public class SocketReactionPostMessage {
    private UUID postId;
    private int emoji;
    private int reactionCount;
    private Date timestamp;
}
