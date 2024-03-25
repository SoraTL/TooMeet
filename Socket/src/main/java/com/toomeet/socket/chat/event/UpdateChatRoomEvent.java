package com.toomeet.socket.chat.event;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateChatRoomEvent {
    private Long roomId;
    private Date updatedAt;
    private Long updatedBy;
    private Icon icon;
    private String name;
    private String color;
    private String avatar;


    public enum Icon {
        LIKE,
        LOVE,
        HAHA,
    }
}
