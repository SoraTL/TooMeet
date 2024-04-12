package com.toomet.chat.reaction.pub;

import com.toomet.chat.reaction.Reaction;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class MessageReactionPublic {
    private Long messageId;
    private Long memberId;
    private Long roomId;
    private Date timestamp;
    private Type type;
    private Reaction.ReactionType reactionType;

    public enum Type {
        CREATE,
        REMOVE
    }
}
