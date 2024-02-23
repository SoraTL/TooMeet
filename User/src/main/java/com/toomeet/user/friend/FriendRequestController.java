package com.toomeet.user.friend;


import com.toomeet.user.friend.dto.AddFriendRequestDto;
import com.toomeet.user.friend.dto.FriendRequestResponseDto;
import com.toomeet.user.friend.dto.ReplyAddFriendDto;
import com.toomeet.user.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("users/friends/request")
public class FriendRequestController {
    private final FriendRequestService friendRequestService;

    @PostMapping("/add")
    public ResponseEntity<String> addFriend(@AuthenticationPrincipal User user, @RequestBody @Valid AddFriendRequestDto dto) {
        String response = friendRequestService.addFriend(user, dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reply")
    public ResponseEntity<String> replyFriend(@AuthenticationPrincipal User user, @RequestBody @Valid ReplyAddFriendDto dto) {
        String message;

        if (dto.getType() == ReplyAddFriendDto.Type.ACCEPTED) message = friendRequestService.acceptFriend(user, dto);
        else message = friendRequestService.rejectFriend(user, dto);

        return new ResponseEntity<>(message, HttpStatus.OK);

    }

    @GetMapping("/sent")
    public ResponseEntity<List<FriendRequestResponseDto>> getSentFriendRequests(@AuthenticationPrincipal User user) {
        List<FriendRequestResponseDto> requests = friendRequestService.getSentFriendRequests(user);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @GetMapping("/received")
    public ResponseEntity<List<FriendRequestResponseDto>> getReceivedFriendRequests(@AuthenticationPrincipal User user) {
        List<FriendRequestResponseDto> requests = friendRequestService.getReceivedFriendRequests(user);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }
}
