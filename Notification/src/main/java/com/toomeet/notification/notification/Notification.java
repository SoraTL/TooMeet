package com.toomeet.notification.notification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private Sender sender;

    @Embedded
    private Receiver receiver;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String content;

    @CreationTimestamp
    private Date timestamp;

}
