package com.toomeet.user.friend;

import com.toomeet.user.friend.dto.FriendResponseDto;
import com.toomeet.user.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("users/friends")
public class FriendController {
    private final FriendService friendService;


    @GetMapping()
    public ResponseEntity<Page<FriendResponseDto>> getAllFriend(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "p", required = false, defaultValue = "0") int page,
            @RequestParam(value = "l", required = false, defaultValue = "10") int limit
    ) {
        Page<FriendResponseDto> friends = friendService.getAllFriend(user, page, limit);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @GetMapping("/online")
    public ResponseEntity<Page<FriendResponseDto>> getOnlineFriends(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "p", required = false, defaultValue = "0") int page,
            @RequestParam(value = "l", required = false, defaultValue = "10") int limit) {

        Page<FriendResponseDto> friends = friendService.getOnlineFriends(user, page, limit);

        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @DeleteMapping("del/{friendId}")
    public ResponseEntity<String> removeFriend(@AuthenticationPrincipal User user, @PathVariable Long friendId) {
        String message = friendService.removeFriend(user, friendId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<FriendResponseDto>> searchFriend(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "p", required = false, defaultValue = "0") int page,
            @RequestParam(value = "l", required = false, defaultValue = "10") int limit
    ) {
        Page<FriendResponseDto> friends = friendService.searchFriend(user, keyword, page, limit);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

}
