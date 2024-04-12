package com.toomeet.user.user;

import com.toomeet.user.auth.Account;
import com.toomeet.user.auth.AccountService;
import com.toomeet.user.auth.dto.AccountResponseDto;
import com.toomeet.user.user.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final ModelMapper mapper;
    private final UserService userService;
    private final AccountService accountService;

    @GetMapping("info/{userId}")
    public ResponseEntity<UserInfo> getOverview(@PathVariable() Long userId) {
        UserInfo userInfo = userService.getUserInfo(userId);
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    @GetMapping("suggestions")
    public ResponseEntity<Page<UserInfo>> getSuggestionsUser(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "p", required = false, defaultValue = "0") int page,
            @RequestParam(value = "l", required = false, defaultValue = "10") int limit) {
        Page<UserInfo> users = userService.getSuggestionsUser(user, page, limit);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("account")
    public ResponseEntity<AccountResponseDto> getAccount(
            @AuthenticationPrincipal User user
    ) {
        Account account = accountService.getAccountById(user.getAccount().getId());
        AccountResponseDto accountResponse = mapper.map(account, AccountResponseDto.class);
        return new ResponseEntity<>(accountResponse, HttpStatus.OK);
    }

}
