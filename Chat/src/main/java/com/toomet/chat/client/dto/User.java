package com.toomet.chat.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Long id;
    private String name;
    private String avatar;

    public static User convertFromUserClientResponse(UserClientResponseDto userInfo) {
        return User.builder()
                .id(userInfo.getId())
                .name(userInfo.getName())
                .avatar(userInfo.getProfile().getAvatar().getUrl())
                .build();
    }
}
