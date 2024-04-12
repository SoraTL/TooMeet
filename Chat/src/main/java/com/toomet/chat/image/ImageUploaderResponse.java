package com.toomet.chat.image;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageUploaderResponse {
    private String publicId;
    private Image.Format format;
    private String url;
}
