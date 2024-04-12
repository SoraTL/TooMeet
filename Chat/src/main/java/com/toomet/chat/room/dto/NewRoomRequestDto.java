package com.toomet.chat.room.dto;

import com.toomet.chat.message.Message;
import com.toomet.chat.room.Room;
import com.toomet.chat.validator.EnumValidator;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class NewRoomRequestDto {
    @NotNull(message = "Tên phòng không được bỏ trống")
    @NotEmpty(message = "Tên phòng không được bỏ trống")
    @Size(min = 5, message = "Tên phòng phải có ít nhất 5 kí tự")
    private String name;

    @EnumValidator(enumClass = Room.RoomType.class, message = "Loại phòng không hợp lệ")
    private Room.RoomType type;

    private Set<Long> member;

    @NotEmpty
    @Builder.Default
    private String color = "#1d4ed8";

    @Builder.Default
    private Message.Icon icon = Message.Icon.LIKE;
}
