package com.toomet.chat.room;

import com.toomet.chat.exceptions.ErrorResponse;
import com.toomet.chat.room.dto.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("chats/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;


    @PostMapping()
    public ResponseEntity<NewRoomResponseDto> createRoom(
            @RequestHeader("x-user-id") Long userId,
            @RequestBody @Valid NewRoomRequestDto dto
    ) throws IOException {
        dto.getMember().add(userId);
        NewRoomResponseDto room = roomService.createNewRoom(dto);
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }


    @PostMapping("{roomId}/add-member")
    public ResponseEntity<String> addMember(
            @RequestHeader("x-user-id") Long userId,
            @PathVariable Long roomId,
            @RequestBody @Valid AddMemberDto dto
    ) {
        String message = roomService.addMember(roomId, userId, dto);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("{roomId}/join-token")
    public ResponseEntity<JoinTokenResponseDto> getJoinToken(
            @PathVariable Long roomId,
            @RequestHeader("x-user-id") Long userId
    ) {
        JoinTokenResponseDto response = roomService.generateJoinToken(roomId, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("{roomId}/join")
    public ResponseEntity<String> joinRoom(
            @PathVariable Long roomId,
            @RequestHeader("x-user-id") Long userId,
            @RequestBody() @Valid JoinRoomDto dto
    ) {
        String message = roomService.joinRoom(roomId, userId, dto);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    @DeleteMapping("{roomId}/leave")
    public ResponseEntity<String> leaveRoom(
            @PathVariable() Long roomId,
            @RequestHeader("x-user-id") Long userId
    ) {
        String message = roomService.leaveRoom(roomId, userId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<RoomResponseDto>> getAllRoom(
            @RequestHeader("x-user-id") Long userId,
            @RequestParam(value = "p", required = false, defaultValue = "0") int page,
            @RequestParam(value = "l", required = false, defaultValue = "20") int limit

    ) {
        Page<RoomResponseDto> rooms = roomService.getAllRoom(userId, page, limit);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponseDto> getRoom(
            @RequestHeader("x-user-id") Long userId,
            @PathVariable Long roomId
    ) {
        RoomResponseDto room = roomService.getRoomByIdAndUserId(roomId, userId);

        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @GetMapping("/settings/{roomId}")
    public ResponseEntity<RoomSettingResponseDto> getRoomSetting(
            @RequestHeader("x-user-id") Long userId,
            @PathVariable Long roomId
    ) {
        RoomSettingResponseDto roomSetting = roomService.getRoomSetting(roomId, userId);
        return new ResponseEntity<>(roomSetting, HttpStatus.OK);
    }


    @GetMapping("/search")
    public ResponseEntity<List<RoomResponseDto>> searchRoom(
            @RequestHeader("x-user-id") Long userId,
            @RequestParam(value = "keyword") String keyword
    ) {
        List<RoomResponseDto> rooms = roomService.searchRoom(userId, keyword);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @PatchMapping("/{roomId}/update/avatar")
    public ResponseEntity<String> updateAvatar(
            @PathVariable Long roomId,
            @RequestHeader("x-user-id") Long userId,
            @RequestParam("avatar") MultipartFile avatar
    ) {
        String newUrl = roomService.updateAvatar(roomId, userId, avatar);
        return new ResponseEntity<>(newUrl, HttpStatus.OK);
    }

    @PatchMapping("{roomId}/update/name")
    public ResponseEntity<String> updateName(
            @PathVariable Long roomId,
            @RequestHeader("x-user-id") Long userId,
            @RequestBody() @Valid UpdateRoomNameDto dto
    ) {
        String newName = roomService.updateName(roomId, userId, dto);
        return new ResponseEntity<>(newName, HttpStatus.OK);
    }

    @PatchMapping("{roomId}/update/setting")
    public ResponseEntity<RoomSettingResponseDto> updateSetting(
            @PathVariable Long roomId,
            @RequestHeader("x-user-id") Long userId,
            @RequestBody() @Valid UpdateSettingDto dto
    ) {
        RoomSettingResponseDto responseDto = roomService.updateSetting(roomId, userId, dto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/{roomId}/images")
    public ResponseEntity<Page<RoomImageResponseDto>> getAllRoomImage(
            @PathVariable Long roomId,
            @RequestHeader("x-user-id") Long userId,
            @RequestParam(value = "p", required = false, defaultValue = "0") int page,
            @RequestParam(value = "l", required = false, defaultValue = "20") int limit
    ) {
        Page<RoomImageResponseDto> roomImages = roomService.getAllRoomImage(roomId, userId, page, limit);
        return new ResponseEntity<>(roomImages, HttpStatus.OK);
    }


    @ExceptionHandler({SignatureException.class})
    public ResponseEntity<ErrorResponse> JwtException(SignatureException exception) {

        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("ROOM_TOKEN_ERROR: " + exception)
                .status(status)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }
}
