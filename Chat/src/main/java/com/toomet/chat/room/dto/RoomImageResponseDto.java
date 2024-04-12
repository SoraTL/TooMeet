package com.toomet.chat.room.dto;

import com.toomet.chat.image.dto.ImageResponseDto;
import com.toomet.chat.message.MessageImage;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomImageResponseDto {
    private Long senderId;
    private ImageResponseDto image;

    public static RoomImageResponseDto convertFromMessageImage(MessageImage messageImage) {
        return RoomImageResponseDto.builder()
                .senderId(messageImage.getMember().getId())
                .image(ImageResponseDto.convertFromImage(messageImage.getImage()))
                .build();
    }
}
