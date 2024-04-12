package com.toomet.chat.message.dto;

import com.toomet.chat.message.Message;
import com.toomet.chat.validator.EnumValidator;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateMessageDto {
    @Size(min = 1, max = 5000, message = "Tin nhắn văn bản phải có độ dài trong khoản [1-5000] kí tự.")
    private String text;

    @EnumValidator(enumClass = Message.Icon.class)
    private Message.Icon icon;

}
