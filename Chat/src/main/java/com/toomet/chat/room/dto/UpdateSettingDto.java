package com.toomet.chat.room.dto;

import com.toomet.chat.message.Message;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdateSettingDto {
    @NotEmpty
    private String color;
    private Message.Icon icon;
}
