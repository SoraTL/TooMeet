package com.toomeet.user.auth;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.google.gson.Gson;
import com.toomeet.user.auth.dto.*;
import com.toomeet.user.exceptions.BadRequestException;
import com.toomeet.user.exceptions.ConflictException;
import com.toomeet.user.exceptions.ForbiddenException;
import com.toomeet.user.exceptions.NotFoundException;
import com.toomeet.user.image.Format;
import com.toomeet.user.image.Image;
import com.toomeet.user.jwt.JwtService;
import com.toomeet.user.mail.MailService;
import com.toomeet.user.mail.OtpMail;
import com.toomeet.user.user.Profile;
import com.toomeet.user.user.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final Jedis jedis;
    private final Gson gson;
    private final OTPService otpService;
    private final MailService mailService;
    private final JwtService jwtService;
    private final AccountService accountService;


    public AuthenticationResponseDto register(AccountRegisterDto dto) {
        // Check if the user already exists in the repository by email
        Optional<Account> accountOptional = accountService.getAccountByEmail(dto.getEmail());

        // If the user exists, throw a ConflictException with an appropriate message
        if (accountOptional.isPresent()) throw new ConflictException("Tài khoản đã tồn tại");

        // Hash the user's password before saving it
        String hashPassword = passwordEncoder.encode(dto.getPassword());
        dto.setPassword(hashPassword);

        String accountCacheId = NanoIdUtils.randomNanoId();

        final long otpExpiredTime = otpService.getExpiredTime();
        final long accountExpiredTime = otpExpiredTime * 5;

        String accountJson = gson.toJson(dto);
        jedis.set(accountCacheId, accountJson);
        jedis.pexpire(accountCacheId, accountExpiredTime);

        String otp = otpService.generateOTP();
        String otpId = otpService.cacheOtp(otp, otpExpiredTime);

        // Send OTP to the user's email address
        otpService.sendOtp(dto.getEmail(), OtpMail
                .builder()
                .subject("Xác minh tài khoản TooMeet") // Verify TooMeet account
                .message("Mã xác thực của bạn là: " + otp) // Your verification code is:
                .build());

        // Return authentication response containing OTP ID, account ID, and expiration time
        return AuthenticationResponseDto.builder()
                .otpId(otpId)
                .accountId(accountCacheId)
                .expiredIn(otpExpiredTime)
                .build();
    }

    public ResendOtpResponseDto resendOtp(String preOtpId, String accountId) {
        // Retrieve account information from cache using the provided account ID
        String accountJson = jedis.get(accountId);

        // If account information is not found, throw a BadRequestException with an appropriate message
        if (accountJson == null) {
            throw new BadRequestException("Gửi OTP thất bại :: thông tin không hợp lệ!"); // Failed to send OTP :: invalid information!
        }

        // Deserialize the JSON account information into an Account object
        AccountRegisterDto accountRegisterDto = gson.fromJson(accountJson, AccountRegisterDto.class);

        // Get the new expiration time for the OTP
        long newExpiredTime = otpService.getExpiredTime();

        // Remove the previous OTP from cache using its ID
        jedis.del(preOtpId);

        // Generate a new OTP and cache it with the new expiration time
        String otp = otpService.generateOTP(6); // Generate OTP with 6 digits
        String otpId = otpService.cacheOtp(otp, newExpiredTime);

        // Send the new OTP to the user's email address
        mailService.sendMail(
                OtpMail
                        .builder()
                        .subject("Xác minh tài khoản TooMeet") // Verify TooMeet account
                        .message("Mã xác thực của bạn là: " + otp) // Your verification code is:
                        .build()
                , accountRegisterDto.getEmail());

        // Return response DTO containing updated OTP information
        return ResendOtpResponseDto
                .builder()
                .email(accountRegisterDto.getEmail())
                .expireIn(newExpiredTime)
                .otpId(otpId)
                .accountId(accountId)
                .build();
    }


    public Object login(AccountLoginDto dto) {
        // Retrieve the account information by email
        Account account = accountService.getAccountByEmail(dto.getEmail())
                .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại")); // Throw exception if account does not exist

        // Check if the provided password matches the stored password for the account
        if (!passwordEncoder.matches(dto.getPassword(), account.getPassword())) {
            throw new ForbiddenException("Sai mật khẩu"); // Throw exception if password is incorrect
        }

        // If 2FA is enabled for the account
        if (account.is2FA()) {
            return handle2Fa(account);
        }

        // Handle non 2FA authentication
        return handleNon2Fa(account);
    }


    public AuthenticatedResponseDto verifyOtpAndCreateUser(String otpId, String accountId, VerifyOtpRequestDto dto) {
        verifyOtp(otpId, dto);
        if (accountId == null) {
            throw new BadRequestException("Không tìm thấy thông tin xác thực cho yêu cầu này.");
        }

        String accountJson = jedis.get(accountId);
        AccountRegisterDto accountRegisterDto = gson.fromJson(accountJson, AccountRegisterDto.class);

        jedis.del(accountId);

        Account account = Account.builder()
                .email(accountRegisterDto.getEmail())
                .password(accountRegisterDto.getPassword())
                .build();

        String avatar = "https://fiverr-res.cloudinary.com/images/t_main1,q_auto,f_auto,q_auto,f_auto/gigs2/292600815/original/46fe8a85183ee3d1d7965c4fad9042ec83bb0875/transform-your-image-into-stunning-ai-generated-masterpiece.png";

        User user = User.builder()
                .name(accountRegisterDto.getName())
                .profile(Profile.builder()
                        .dateOfBirth(accountRegisterDto.getDateOfBirth())
                        .gender(accountRegisterDto.getGender())
                        .avatar(Image.builder()
                                .cloudPublicId("test")
                                .format(Format.PNG)
                                .url(avatar)
                                .build())
                        .build())
                .account(account)
                .build();


        account.setUser(user);

        Account newAccount = saveAccount(account);
        String token = jwtService.generateToken(newAccount);

        AccountResponseDto accountResponse = mapper.map(newAccount, AccountResponseDto.class);
        
        return AuthenticatedResponseDto
                .builder()
                .account(accountResponse)
                .token(token)
                .expireIn(jwtService.getTokenExpiredTime())
                .build();
    }

    public AuthenticatedResponseDto verifyOtpAndLogin(String otpId, String accountId, VerifyOtpRequestDto dto) {
        verifyOtp(otpId, dto);
        String accountJson = jedis.get(accountId);
        AccountResponseDto accountResponse = gson.fromJson(accountJson, AccountResponseDto.class);

        jedis.del(accountId);

        String token = jwtService.generateToken(mapper.map(accountResponse, Account.class));
        return AuthenticatedResponseDto
                .builder()
                .account(accountResponse)
                .token(token)
                .expireIn(jwtService.getTokenExpiredTime())
                .build();
    }

    private void verifyOtp(String otpId, VerifyOtpRequestDto dto) {
        if (!otpService.validateOTP(otpId, dto.getOtp())) {
            throw new ForbiddenException("OPT không hợp lệ hoặc đã hết hạn. vui lòng đăng nhập và thử lại sau.");
        }
        jedis.del(otpId);
    }

    private Account saveAccount(Account account) {
        List<AccountRole.Role> roles = List.of(
                AccountRole.Role.NORMAL_USER
        );

        account.setRoles(
                roles.stream()
                        .map(role -> AccountRole
                                .builder()
                                .role(role)
                                .account(account)
                                .build()
                        ).toList()
        );

        return accountService.saveAccount(account);
    }

    private Object handleNon2Fa(Account account) {

        // Map the Account object to AccountResponseDto
        AccountResponseDto accountResponse = mapper.map(account, AccountResponseDto.class);

        // Generate JWT token for authentication
        String token = jwtService.generateToken(account);
        return AuthenticatedResponseDto
                .builder()
                .account(accountResponse)
                .token(token)
                .expireIn(jwtService.getTokenExpiredTime())
                .build();
    }

    private Object handle2Fa(Account account) {

        // Map the Account object to AccountResponseDto
        AccountResponseDto accountResponse = mapper.map(account, AccountResponseDto.class);
        String otp = otpService.generateOTP();
        Long otpExpiredTime = otpService.getExpiredTime();
        long profileExpiredTime = otpExpiredTime * 5;

        // Cache OTP and set expiration time
        String otpId = otpService.cacheOtp(otp, otpExpiredTime);

        // Cache AccountResponseDto with profile expiration time
        String accountResponseJson = gson.toJson(accountResponse);
        jedis.set(accountResponse.getId(), accountResponseJson);
        jedis.pexpire(accountResponse.getId(), profileExpiredTime);

        // Send OTP to the user's email address
        otpService.sendOtp(accountResponse.getEmail(), OtpMail
                .builder()
                .subject("Xác minh 2 bước tài khoản TooMeet") // Two-step verification for TooMeet account
                .message("Mã xác thực của bạn là: " + otp) // Your verification code is:
                .build());

        // Return authentication response containing account ID, OTP ID, and expiration time
        return AuthenticationResponseDto.builder()
                .accountId(accountResponse.getId())
                .otpId(otpId)
                .expiredIn(otpExpiredTime)
                .build();
    }

}
