package com.toomeet.user;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.toomeet.user.auth.Account;
import com.toomeet.user.auth.AccountRole;
import com.toomeet.user.auth.AccountService;
import com.toomeet.user.image.Format;
import com.toomeet.user.image.Image;
import com.toomeet.user.user.Gender;
import com.toomeet.user.user.Profile;
import com.toomeet.user.user.User;
import com.toomeet.user.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;


@SpringBootApplication
@RestController
@Slf4j
public class UserApplication implements CommandLineRunner {

    @Autowired
    PasswordEncoder passwordEncoders;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountService accountService;

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {

//        test(5);
        System.out.println("success");
    }

    private void saveAccount(Account account) {
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

        accountService.saveAccount(account);
        System.out.println("email: " + account.getEmail());
        System.out.println("password: " + account.getPassword());
    }

    private void test(int count) {
        for (int i = 0; i < count; ++i) {
            String username = NanoIdUtils.randomNanoId();
            String email = username + "@gmail.com";
            Account account = Account.builder()
                    .email(email)
                    .password(passwordEncoders.encode(email))
                    .build();

            String avatar = "https://ik.imagekit.io/freeflo/production/24b2bc0e-6d28-4018-8a2d-fa9b34427864.png?tr=w-1920,q-75&alt=media&pr-true";

            User user = User.builder()
                    .name("sandbox- " + i + username)
                    .profile(Profile.builder()
                            .dateOfBirth(new Date())
                            .gender(Gender.MALE)
                            .avatar(Image.builder()
                                    .cloudPublicId(NanoIdUtils.randomNanoId())
                                    .format(Format.PNG)
                                    .url(avatar)
                                    .build())
                            .build())
                    .account(account)
                    .build();

            account.setUser(user);

            saveAccount(account);
            System.out.println("Them user " + (i + 1) + "Thanh cong!");
        }
    }

}



