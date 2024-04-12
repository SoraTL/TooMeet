package com.toomeet.user.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String authorEmail;

    public void sendMail(OtpMail mail, String mailTo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(authorEmail);
        message.setSubject(mail.getSubject());
        message.setText(mail.getMessage());
        message.setTo(mailTo);
        mailSender.send(message);
    }
}
