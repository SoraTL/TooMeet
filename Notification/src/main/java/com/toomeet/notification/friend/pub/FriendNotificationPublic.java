package com.toomeet.notification.friend.pub;

import com.toomeet.notification.notification.NotificationType;
import com.toomeet.notification.notification.Receiver;
import com.toomeet.notification.notification.Sender;
import lombok.Data;

import java.util.Date;

@Data
public class FriendNotificationPublic {
    private Long id;
    private Sender sender;
    private Receiver receiver;
    private String content;
    private NotificationType type;
    private Date timestamp;
}
