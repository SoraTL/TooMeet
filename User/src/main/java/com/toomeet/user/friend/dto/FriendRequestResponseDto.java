package com.toomeet.user.friend.dto;

import lombok.Data;

import java.util.Date;

@Data
public class FriendRequestResponseDto {
    private Long id;
    private FriendResponseDto sender;
    private FriendResponseDto receiver;
    private Date timeStamp;
}
