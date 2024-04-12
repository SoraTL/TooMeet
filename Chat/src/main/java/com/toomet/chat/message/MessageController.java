package com.toomet.chat.message;

import com.toomet.chat.message.dto.CreateMessageDto;
import com.toomet.chat.message.dto.MessageResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/chats/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;


    @PostMapping("/image")
    public ResponseEntity<MessageResponseDto> createMessageImage(
            @RequestHeader("x-user-id") Long userId,
            @RequestParam(value = "r") Long roomId,
            @RequestParam(value = "image") MultipartFile image
    ) {
        MessageResponseDto responseDto = messageService.createMessageImage(roomId, userId, image);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping()
    public ResponseEntity<MessageResponseDto> createMessage(
            @RequestHeader("x-user-id") Long senderId,
            @RequestBody @Valid CreateMessageDto dto,
            @RequestParam(value = "r") Long roomId,
            @RequestParam(value = "reply", required = false) Long reply
    ) {

        MessageResponseDto messageResponse;

        if (reply != null) {
            messageResponse = messageService.replyMessage(senderId, reply, roomId, dto);
        } else {
            messageResponse = messageService.createMessage(senderId, roomId, dto);
        }

        return new ResponseEntity<>(messageResponse, HttpStatus.CREATED);
    }


    @GetMapping()
    public ResponseEntity<Page<MessageResponseDto>> getAllMessageInRoom(
            @RequestHeader("x-user-id") Long userId,
            @RequestParam(value = "r") Long roomId,
            @RequestParam(value = "p", required = false, defaultValue = "0") int page,
            @RequestParam(value = "l", required = false, defaultValue = "20") int limit
    ) {
        Page<MessageResponseDto> messages = messageService.getAllMessageInRoom(userId, roomId, page, limit);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("{roomId}/latest")
    public ResponseEntity<MessageResponseDto> getLatestMessage(
            @PathVariable Long roomId,
            @RequestHeader("x-user-id") Long userId
    ) {
        MessageResponseDto messageResponse = messageService.getLatestMessage(roomId, userId);
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }


    @DeleteMapping("{messageId}")
    public ResponseEntity<MessageResponseDto> recallMessage(
            @RequestHeader("x-user-id") Long userId,
            @RequestParam(value = "r") Long roomId,
            @PathVariable Long messageId
    ) {
        MessageResponseDto messageResponse = messageService.recallMessage(userId, roomId, messageId);
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }


}
