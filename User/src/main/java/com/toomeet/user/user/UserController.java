package com.toomeet.user.user;

import com.toomeet.user.user.dto.UserOverviewDto;
import com.toomeet.user.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final ModelMapper mapper;
    private final UserService userService;


    @GetMapping("info")
    public ResponseEntity<UserResponseDto> getUserInfo(@AuthenticationPrincipal User user) {
        UserResponseDto userResponse = mapper.map(user, UserResponseDto.class);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping("suggestions")
    public ResponseEntity<Page<UserOverviewDto>> getSuggestionsUser(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "p", required = false, defaultValue = "0") int page,
            @RequestParam(value = "l", required = false, defaultValue = "10") int limit) {
        Page<UserOverviewDto> users = userService.getSuggestionsUser(user, page, limit);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
