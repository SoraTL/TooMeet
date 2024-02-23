package com.toomeet.user.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticatedResponseDto {
    private AccountResponseDto account;
    private String token;
    private Long expireIn;
    @Builder.Default
    private String tokenType = "Bearer";
}
