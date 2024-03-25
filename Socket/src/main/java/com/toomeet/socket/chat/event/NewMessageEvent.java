package com.toomeet.socket.chat.event;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class NewMessageEvent {
    private Long id;
    private Long senderId;
    private Long roomId;
    private Icon icon;
    private String text;
    private MessageReply reply;
    private String image;
    @Builder.Default
    private List<Reaction> reactions = new ArrayList<>();
    private boolean isRecall;
    @Builder.Default
    private List<Long> viewedMembers = new ArrayList<>();
    private Date timestamp;
    private Status status;


    public enum Icon {
        LIKE,
        HAHA,
        LOVE
    }

    public enum ReactionType {
        LIKE,
        HAHA,
        LOVE,
        SAD,
        WOW,
        ANGRY
    }

    public enum Status {
        SENT,
        RECEIVED,
        VIEWED
    }


    @Data
    public static class MessageReply {
        private Long id;
        private Long senderId;
        private Icon icon;
        private String text;
        private String image;
        private Date timestamp;
    }

    @Data
    public static class Reaction {
        private Integer count;
        private Date timestamp;
        private Long memberId;
        private ReactionType type;

    }
}
