package com.toomeet.user.user.dto;

import com.toomeet.user.image.dto.ImageResponseDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserOverviewDto {
    private Long id;
    private String name;
    private Profile profile;

    @Data
    public static class Profile {
        private String description;
        private ImageResponseDto avatar;
    }

}
