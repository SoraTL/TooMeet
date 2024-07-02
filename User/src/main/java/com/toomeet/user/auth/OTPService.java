package com.toomeet.user.auth;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.google.gson.Gson;
import com.toomeet.user.mail.MailService;
import com.toomeet.user.mail.OtpMail;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Random;

@Service
@Getter
@RequiredArgsConstructor
public class OTPService {
    private final Random random = new Random();
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final Jedis jedis;
    private final Gson gson;

    @Value("${otp.expired_time}")
    private Long expiredTime;

    public String generateOTP(int len) {
        String alphabets = "1234567890abcdefghijklmnopqrstuvwxyz";
        return NanoIdUtils.randomNanoId(random, alphabets.toCharArray(), len);
    }

    public String generateOTP() {
        return this.generateOTP(6);
    }

    public void sendOtp(String email, OtpMail otpMail) {
        mailService.sendMail(otpMail, email);
    }

    public String cacheOtp(String otp, Long expiredTime) {
        String hashOtp = passwordEncoder.encode(otp);
        String otpJson = gson.toJson(hashOtp);
        String otpId = NanoIdUtils.randomNanoId();
        jedis.set(otpId, otpJson);
        jedis.pexpire(otpId, expiredTime);
        return otpId;
    }


    public boolean validateOTP(String otpId, String otp) {
        String otpJson = jedis.get(otpId);
        if (otpJson == null) return false;
        String otpHash = gson.fromJson(otpJson, String.class);
        return isValidOtp(otp, otpHash);
    }

    private boolean isValidOtp(String rawOtp, String otp) {
        return passwordEncoder.matches(rawOtp, otp);
    }


}
