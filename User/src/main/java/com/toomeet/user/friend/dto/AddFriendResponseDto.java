package com.toomeet.user.friend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddFriendResponseDto {
    private Long requestId;
    private String message;
}
