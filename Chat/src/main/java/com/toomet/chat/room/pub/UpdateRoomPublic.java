package com.toomet.chat.room.pub;

import com.toomet.chat.message.Message;
import com.toomet.chat.room.Room;
import com.toomet.chat.room.RoomSetting;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UpdateRoomPublic {
    private Long roomId;
    private Date updatedAt;
    private Long updatedBy;
    private Message.Icon icon;
    private String name;
    private String color;
    private String avatar;

    public static UpdateRoomPublic convertFromRoom(Room room, Long updatedBy) {
        RoomSetting roomSetting = room.getSetting();
        return UpdateRoomPublic.builder()
                .name(room.getName())
                .roomId(room.getId())
                .avatar(room.getAvatar().getUrl())
                .color(roomSetting.getColor())
                .icon(roomSetting.getIcon())
                .updatedBy(updatedBy)
                .updatedAt(room.getUpdatedAt())
                .build();
    }
    
}
