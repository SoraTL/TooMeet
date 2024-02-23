package com.toomeet.user.friend;

import com.toomeet.user.friend.dto.FriendResponseDto;
import com.toomeet.user.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("users/friends")
public class FriendController {
    private final FriendService friendService;

    @GetMapping()
    public ResponseEntity<List<FriendResponseDto>> getAllFriend(@AuthenticationPrincipal User user) {
        List<FriendResponseDto> friends = friendService.getAllFriend(user);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }


    @DeleteMapping("del/{friendId}")
    public ResponseEntity<String> removeFriend(@AuthenticationPrincipal User user, @PathVariable Long friendId) {
        String message = friendService.removeFriend(user, friendId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


}
