package com.toomeet.user.user.dto;

import com.toomeet.user.image.dto.ImageResponseDto;
import com.toomeet.user.user.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private Long id;
    private String name;
    private Profile profile;
    private Status status;

    @Data
    public static class Profile {
        private String description;
        private ImageResponseDto avatar;
    }

}
