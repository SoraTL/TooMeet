package com.toomet.chat.room.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoomNameDto {
    @NotEmpty
    @NotNull
    private String newName;
}
