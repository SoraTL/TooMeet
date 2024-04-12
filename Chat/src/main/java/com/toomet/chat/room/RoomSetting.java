package com.toomet.chat.room;

import com.toomet.chat.message.Message;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Embeddable
@Data
public class RoomSetting {
    @Enumerated(EnumType.STRING)
    private Message.Icon icon = Message.Icon.LIKE;
    private String color = "#1d4ed8";
}
