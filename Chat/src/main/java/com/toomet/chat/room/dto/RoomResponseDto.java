package com.toomet.chat.room.dto;

import com.toomet.chat.client.dto.User;
import com.toomet.chat.room.Room;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RoomResponseDto {
    private Long id;
    private String name;
    private String avatar;
    private Room.RoomType type;
    private String displayName;
    private Date updatedAt;


    public static RoomResponseDto convertFromRoom(Room room) {
        return RoomResponseDto.builder()
                .type(room.getType())
                .name(room.getName())
                .id(room.getId())
                .displayName(room.getName())
                .avatar(room.getAvatar().getUrl())
                .updatedAt(room.getUpdatedAt())
                .build();
    }

    public static RoomResponseDto convertToSingleRoom(Room room, User user) {
        return RoomResponseDto.builder()
                .type(room.getType())
                .name(user.getName())
                .id(room.getId())
                .displayName(user.getName())
                .avatar(user.getAvatar())
                .updatedAt(room.getUpdatedAt())
                .build();
    }


}
