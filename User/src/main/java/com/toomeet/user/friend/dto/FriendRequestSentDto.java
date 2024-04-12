package com.toomeet.user.friend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class FriendRequestSentDto {
    @JsonProperty("requestId")
    private Long id;
    private FriendResponseDto receiver;
    private String message;
    private Date timeStamp;
}
