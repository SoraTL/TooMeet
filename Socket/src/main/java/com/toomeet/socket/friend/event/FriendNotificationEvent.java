package com.toomeet.socket.friend.event;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class FriendNotificationEvent {
    private Long id;
    private Sender sender;
    private Receiver receiver;
    private String content;
    private NotificationType type;
    private Date timestamp;

    public enum NotificationType {
        GROUP,
        COMMENT,
        SYSTEM,
        FRIEND,
        POST
    }

    @Data
    @Builder
    public static class Sender {
        private Long id;
        private String avatar;
        private String name;
    }

    @Data
    
    public static class Receiver {
        private Long id;
        private String avatar;
        private String name;
    }


}
