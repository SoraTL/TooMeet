package com.toomeet.user.friend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class FriendRequestReceivedDto {
    @JsonProperty("requestId")
    private Long id;
    private FriendResponseDto sender;
    private String message;
    private Date timeStamp;
}
