package com.toomet.chat.image.dto;

import com.toomet.chat.image.Image;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ImageResponseDto {
    private String url;
    private Date createdAt;
    private Date updatedAt;

    public static ImageResponseDto convertFromImage(Image image) {
        return ImageResponseDto.builder()
                .url(image.getUrl())
                .createdAt(image.getCreatedAt())
                .updatedAt(image.getUpdatedAt())
                .build();
    }

}
