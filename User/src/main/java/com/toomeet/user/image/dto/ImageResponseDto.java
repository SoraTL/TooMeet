package com.toomeet.user.image.dto;

import com.toomeet.user.image.Format;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponseDto {
    //    private Long id;
    //    private String cloudPublicId;
    private String url;
    private Format format;
    private Date createdAt;
    private Date updatedAt;
}
