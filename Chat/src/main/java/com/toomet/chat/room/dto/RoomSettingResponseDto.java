package com.toomet.chat.room.dto;

import com.toomet.chat.message.Message;
import com.toomet.chat.room.Room;
import com.toomet.chat.room.RoomSetting;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomSettingResponseDto {
    private Long roomId;
    private Message.Icon icon;
    private String color;

    public static RoomSettingResponseDto convertFromRoom(Room room) {
        RoomSetting setting = room.getSetting();
        return RoomSettingResponseDto
                .builder()
                .roomId(room.getId())
                .color(setting.getColor())
                .icon(setting.getIcon())
                .build();
    }
}
