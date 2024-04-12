package com.toomeet.user.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponseDto {
    private String otpId;
    private String accountId;
    private Long expiredIn;
}
