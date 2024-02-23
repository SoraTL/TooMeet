package com.toomeet.user;

import com.toomeet.user.auth.Account;
import com.toomeet.user.auth.AccountRole;
import com.toomeet.user.auth.AccountService;
import com.toomeet.user.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

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

//
//        for (int i = 0; i < 20; ++i) {
//            String email = NanoIdUtils.randomNanoId() + "@gamil.com";
//            Account account = Account.builder()
//                    .email(email)
//                    .password(passwordEncoders.encode(email))
//                    .build();
//
//            User user = User.builder()
//                    .name("user " + (i + 1))
//                    .profile(Profile.builder()
//                            .dateOfBirth(new Date())
//                            .gender(Gender.MALE)
//                            .build())
//                    .account(account)
//                    .build();
//
//            account.setUser(user);
//
//            saveAccount(account);
//        }
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
    }

}



