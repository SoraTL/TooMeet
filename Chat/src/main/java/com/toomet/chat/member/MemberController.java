package com.toomet.chat.member;

import com.toomet.chat.client.dto.User;
import com.toomet.chat.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/chats/{roomId}/members")
@RequiredArgsConstructor
public class MemberController {
    private final ImageService imageService;
    private final MemberService memberService;

    @GetMapping()
    public ResponseEntity<List<User>> getAllMember(
            @RequestHeader("x-user-id") Long userId,
            @PathVariable Long roomId
    ) {
        List<User> member = memberService.getAllMemberByRoomId(userId, roomId);
        return new ResponseEntity<>(member, HttpStatus.OK);
    }


    @DeleteMapping("delete-message")
    public ResponseEntity<Date> deleteMessage(
            @RequestHeader("x-user-id") Long userId,
            @PathVariable Long roomId
    ) {
        Date timestamp = memberService.deleteMessage(userId, roomId);
        return new ResponseEntity<>(timestamp, HttpStatus.OK);
    }


}
