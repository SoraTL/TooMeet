package com.toomeet.user.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyOtpRequestDto {
    @NotNull
    @Size(min = 6, message = "Otp phải có 6 kí tự")
    @Size(max = 6, message = "Otp phải có 6 kí tự")
    private String otp;
}
