package com.toomet.chat.reaction;

import com.toomet.chat.reaction.dto.ReactionMessageDto;
import com.toomet.chat.reaction.dto.ReactionResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chats/{roomId}/{messageId}/reaction")
@RequiredArgsConstructor
@Validated
public class ReactionController {
    private final ReactionService reactionService;


    @PostMapping()
    public ResponseEntity<ReactionResponseDto> reactionMessage(
            @PathVariable Long messageId,
            @PathVariable Long roomId,
            @RequestHeader("x-user-id") Long userId,
            @RequestBody @Valid ReactionMessageDto dto
    ) {
        ReactionResponseDto reactionResponse = reactionService.reactionMessage(roomId, messageId, userId, dto);
        return new ResponseEntity<>(reactionResponse, HttpStatus.CREATED);
    }


    @DeleteMapping
    public ResponseEntity<ReactionResponseDto> removeReactionMessage(
            @PathVariable Long messageId,
            @PathVariable Long roomId,
            @RequestHeader("x-user-id") Long userId
    ) {
        ReactionResponseDto reactionResponseDto = reactionService.removeReaction(roomId, messageId, userId);
        return new ResponseEntity<>(reactionResponseDto, HttpStatus.OK);
    }

}
