package com.toomeet.user.auth;

import com.toomeet.user.auth.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final AuthService authService;

    @PostMapping("register")
    public ResponseEntity<AuthenticationResponseDto> register(@RequestBody @Valid AccountRegisterDto dto) {
        AuthenticationResponseDto userResponse = authService.register(dto);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @PostMapping("otp/resend")
    public ResponseEntity<ResendOtpResponseDto> resendOtp(@RequestParam("o") String otpId, @RequestParam("a") String accountId) {
        ResendOtpResponseDto resendOtpResponse = authService.resendOtp(otpId, accountId);
        return new ResponseEntity<>(resendOtpResponse, HttpStatus.CREATED);
    }

    @PostMapping("otp/verify")
    public ResponseEntity<AuthenticatedResponseDto> verifyOtp(@RequestParam("o") String otpId, @RequestParam("a") String accountId, @RequestBody @Valid VerifyOtpRequestDto dto) {
        AuthenticatedResponseDto userAuthenticatedResponse = authService.verifyOtpAndCreateUser(otpId, accountId, dto);
        return new ResponseEntity<>(userAuthenticatedResponse, HttpStatus.OK);
    }

    @PostMapping("otp/2fa/verify")
    public ResponseEntity<AuthenticatedResponseDto> verify2FaOtp(@RequestParam("o") String otpId, @RequestParam("a") String accountId, @RequestBody @Valid VerifyOtpRequestDto dto) {
        AuthenticatedResponseDto userAuthenticatedResponse = authService.verifyOtpAndLogin(otpId, accountId, dto);
        return new ResponseEntity<>(userAuthenticatedResponse, HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<Object> login(@RequestBody @Valid AccountLoginDto dto) {
        Object response = authService.login(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
