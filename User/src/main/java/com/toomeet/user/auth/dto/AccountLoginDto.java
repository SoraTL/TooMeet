package com.toomeet.user.auth.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class AccountLoginDto {
    @Email(message = "Email không hợp lệ")
    private String email;
    private String password;
}
