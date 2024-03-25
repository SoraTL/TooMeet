package com.toomeet.user.friend;


import com.toomeet.user.friend.dto.*;
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
    public ResponseEntity<AddFriendResponseDto> addFriend(@AuthenticationPrincipal User user, @RequestBody @Valid AddFriendRequestDto dto) {
        AddFriendResponseDto response = friendRequestService.addFriend(user, dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/cancel/{requestId}")
    public ResponseEntity<String> cancelFriendRequest(@AuthenticationPrincipal User user, @PathVariable Long requestId) {
        String message = friendRequestService.cancelFriendRequest(requestId, user);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/reply")
    public ResponseEntity<String> replyFriend(@AuthenticationPrincipal User user, @RequestBody @Valid ReplyAddFriendDto dto) {
        String message;

        if (dto.getType() == ReplyAddFriendDto.Type.ACCEPTED) message = friendRequestService.acceptFriend(user, dto);
        else message = friendRequestService.rejectFriend(user, dto);

        return new ResponseEntity<>(message, HttpStatus.OK);

    }

    @GetMapping("/sent")
    public ResponseEntity<List<FriendRequestSentDto>> getSentFriendRequests(@AuthenticationPrincipal User user) {
        List<FriendRequestSentDto> requests = friendRequestService.getSentFriendRequests(user);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @GetMapping("/received")
    public ResponseEntity<List<FriendRequestReceivedDto>> getReceivedFriendRequests(@AuthenticationPrincipal User user) {
        List<FriendRequestReceivedDto> requests = friendRequestService.getReceivedFriendRequests(user);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }
}
