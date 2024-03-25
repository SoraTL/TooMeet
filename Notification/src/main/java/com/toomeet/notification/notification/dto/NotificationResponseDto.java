package com.toomeet.notification.notification.dto;

import com.toomeet.notification.notification.NotificationType;
import com.toomeet.notification.notification.Sender;
import lombok.Data;

import java.util.Date;

@Data
public class NotificationResponseDto {
    private Long id;
    private Sender sender;
    private String content;
    private NotificationType type;
    private Date timestamp;
}
