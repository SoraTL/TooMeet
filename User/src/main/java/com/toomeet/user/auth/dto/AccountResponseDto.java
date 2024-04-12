package com.toomeet.user.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.toomeet.user.user.dto.UserResponseDto;
import lombok.Data;

@Data
public class AccountResponseDto {
    @JsonProperty("accountId")
    private String id;
    private String email;
    private UserResponseDto user;
}
