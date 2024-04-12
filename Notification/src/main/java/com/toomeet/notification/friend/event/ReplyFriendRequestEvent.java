package com.toomeet.notification.friend.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyFriendRequestEvent {
    private Long senderId;
    private Long receiverId;
    private Type type;

    public  enum Type {
        REJECTED,
        ACCEPTED
    }
}
