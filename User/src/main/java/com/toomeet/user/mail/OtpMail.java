package com.toomeet.user.mail;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpMail {
    private String subject;
    private String message;
}
