package com.toomeet.user.friend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddFriendRequestDto {
    @NotNull
    private Long receiverId;

    @NotNull
    private String message;
}
