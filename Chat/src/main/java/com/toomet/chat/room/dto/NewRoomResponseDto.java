package com.toomet.chat.room.dto;

import com.toomet.chat.room.Room;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewRoomResponseDto {
    private Long id;
    private String name;
    private String avatar;
    private Room.RoomType type;


    public static NewRoomResponseDto convertFromRoom(Room room) {
        return NewRoomResponseDto.builder()
                .id(room.getId())
                .name(room.getName())
                .avatar(room.getAvatar().getUrl())
                .type(room.getType())
                .build();
    }
}
