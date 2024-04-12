package com.toomeet.notification.friend.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestEvent {
    private Long senderId;
    private Long receiverId;
    private String message;
    private Date timestamp;
}
