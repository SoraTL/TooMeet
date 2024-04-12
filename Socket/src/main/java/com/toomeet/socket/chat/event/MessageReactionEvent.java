package com.toomeet.socket.chat.event;

import lombok.Data;

import java.util.Date;

@Data
public class MessageReactionEvent {
    private Long messageId;
    private Long memberId;
    private Long roomId;
    private Date timestamp;
    private Type type;
    private ReactionType reactionType;

    public enum Type {
        CREATE,
        REMOVE
    }

    public enum ReactionType {
        LIKE,
        HAHA,
        LOVE,
        WOW,
        SAD,
        ANGRY
    }
}
