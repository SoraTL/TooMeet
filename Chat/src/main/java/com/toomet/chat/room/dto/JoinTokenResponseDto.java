package com.toomet.chat.room.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinTokenResponseDto {
    private String token;
    private Long exp;
}
