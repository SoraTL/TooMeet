package com.toomet.chat.room.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JoinRoomDto {
    @NotNull(message = "Token không được bỏ trống")
    private String token;
}
