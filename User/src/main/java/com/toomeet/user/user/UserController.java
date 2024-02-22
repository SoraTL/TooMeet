package com.toomeet.user.user;

import com.toomeet.user.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final ModelMapper mapper;
    private final UserService userService;

    @GetMapping("info")
    public ResponseEntity<UserResponseDto> getUserInfo(@AuthenticationPrincipal User user) {
        UserResponseDto userResponse = mapper.map(user, UserResponseDto.class);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

}
