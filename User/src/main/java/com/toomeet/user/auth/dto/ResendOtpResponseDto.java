package com.toomeet.user.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResendOtpResponseDto {
    private String otpId;
    private String profileId;
    private String email;
    private Long expireIn;
}
